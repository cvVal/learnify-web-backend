package com.demo.learnify.data.repositories

import com.demo.learnify.data.mappers.QuestionDataMapper
import com.demo.learnify.data.models.Question
import io.r2dbc.spi.ConnectionFactory
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.r2dbc.core.DatabaseClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux

interface QuestionRepository : ReactiveCrudRepository<Question, Long>, CustomQuestionRepository {
    fun findAllByQuizId(quizId: Long): Flux<Question>
    fun deleteByQuizId(quizId: Long): Mono<Void>
}

interface CustomQuestionRepository {
    fun saveQuestions(questions: List<Question>): Flux<Question>
    fun updateQuestion(question: Question): Mono<Question>
}

class CustomQuestionRepositoryImpl(
    private val databaseClient: DatabaseClient,
    private val mapper: QuestionDataMapper
    //private val connectionFactory: ConnectionFactory
) : CustomQuestionRepository {

    override fun saveQuestions(questions: List<Question>): Flux<Question> {
        /*return Mono.from(connectionFactory.create())
            .flatMapMany { connection ->
                val batch = connection.createBatch()
                questions.forEach { question ->
                    batch.add(
                        """
                            INSERT INTO questions (
                                quiz_id,
                                question,
                                correct_option
                            )
                            VALUES (
                                ${question.quizId},
                                '${question.question}',
                                '${question.correctOption}'
                            )
                        """.trimIndent()
                    )
                }
                val result = batch.execute()
                result.toFlux().map {
                    it.map(mapper::apply)
                }.let { Flux.concat(it) }
            }*/
        /*return databaseClient.inConnectionMany { connection ->
            val statement = connection.createStatement(INSERT_QUESTION).returnGeneratedValues("id")
            questions.map { question ->
                statement.bind(0, question.quizId)
                    .bind(1, question.question)
                    .bind(2, question.options.toTypedArray())
                    .bind(3, question.correctOption)
                    .add()
            }
            statement.execute()
                .toFlux()
                .flatMap { result ->
                    result.map(mapper::apply)
                }.let { Flux.concat(it) }
        }*/
        val questionFlux = Flux.fromIterable(questions)
        return questionFlux.flatMap { question ->
            databaseClient.sql(INSERT_QUESTION)
                .bind(0, question.quizId)
                .bind(1, question.question)
                .bind(2, question.options.toTypedArray())
                .bind(3, question.correctOption)
                .filter { statement, _ -> statement.returnGeneratedValues("id").execute() }
                .fetch()
                .all()
                .map {
                    question.copy(
                        id = it["id"] as Int
                    )
                }
        }
    }

    override fun updateQuestion(question: Question): Mono<Question> =
        databaseClient.sql(UPDATE_QUESTION)
            .bind("id", question.id)
            .bind("question", question.question)
            .bind("options", question.options.toTypedArray())
            .bind("correctOption", question.correctOption)
            .fetch()
            .rowsUpdated()
            .flatMap { if (it > 0) Mono.just(question) else Mono.empty() }

    companion object {
        const val INSERT_QUESTION = """
            INSERT INTO questions (
                quiz_id,
                question,
                options,
                correct_option
            )
            VALUES (
                $1,
                $2,
                $3,
                $4
            )
        """
        const val UPDATE_QUESTION = """
            UPDATE questions SET
                question = :question,
                options = :options,
                correct_option = :correctOption
            WHERE id = :id
        """
    }
}
