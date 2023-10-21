package com.demo.learnify.data.repositories

import com.demo.learnify.data.mappers.QuizDataMapper
import com.demo.learnify.data.models.Quiz
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.r2dbc.core.DatabaseClient
import reactor.core.publisher.Mono
import java.time.LocalDateTime

interface QuizRepository : ReactiveCrudRepository<Quiz, Long>, CustomQuizRepository

interface CustomQuizRepository {
    fun saveQuiz(quiz: Quiz): Mono<Quiz>
    fun findByQuizIdJoinQuestions(quizId: Long): Mono<Quiz>
}

class CustomQuizRepositoryImpl(
    private val databaseClient: DatabaseClient,
    private val mapper: QuizDataMapper
) : CustomQuizRepository {
    override fun saveQuiz(quiz: Quiz): Mono<Quiz> =
        databaseClient.sql(INSERT_QUIZ)
            .bind("lessonId", quiz.lessonId)
            .filter { statement, _ -> statement.returnGeneratedValues("id", "created_at", "updated_at").execute() }
            .fetch()
            .first()
            .map {
                quiz.copy(
                    id = it["id"] as Int,
                    createdAt = it["created_at"] as LocalDateTime,
                    updatedAt = it["updated_at"] as LocalDateTime
                )
            }

    override fun findByQuizIdJoinQuestions(quizId: Long): Mono<Quiz> =
        databaseClient.sql(SELECT_BY_ID_JOIN_QUESTIONS)
            .bind("id", quizId)
            .map(mapper::apply)
            .first()

    companion object {
        const val INSERT_QUIZ = """
            INSERT INTO quizzes (
                lesson_id
            )
            VALUES (
                :lessonId
            )
        """
        const val SELECT_BY_ID_JOIN_QUESTIONS = """
            SELECT Q.ID,
                Q.LESSON_ID,
                JSON_AGG(JSON_BUILD_OBJECT(
                        'id',
                        QS.ID,
                        'quiz_id',
                        QS.QUIZ_ID,
                        'question',
                        QS.QUESTION,
                        'options',
                        QS.OPTIONS,
                        'correct_option',
                        QS.CORRECT_OPTION)
                    ORDER BY QS.ID) AS QUESTIONS,
                Q.CREATED_AT,
                Q.UPDATED_AT
            FROM QUIZZES Q
            LEFT JOIN QUESTIONS QS ON Q.ID = QS.QUIZ_ID
            WHERE Q.ID = :id
            GROUP BY Q.ID,
                Q.LESSON_ID,
                Q.CREATED_AT,
                Q.UPDATED_AT;
        """
    }
}
