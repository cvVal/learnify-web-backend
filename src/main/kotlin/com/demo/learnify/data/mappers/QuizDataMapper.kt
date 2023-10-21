package com.demo.learnify.data.mappers

import com.demo.learnify.data.models.Question
import com.demo.learnify.data.models.Quiz
import io.r2dbc.spi.Row
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.function.BiFunction

@Component
class QuizDataMapper(
    private val questionDataMapper: QuestionDataMapper
) : BiFunction<Row, Any, Quiz> {

    override fun apply(row: Row, o: Any): Quiz {
        val quizId = row.get("id", Number::class.java)!!.toInt()
        val lessonId = row.get("lesson_id", Number::class.java)!!.toInt()
        val createdAt = row.get("created_at", LocalDateTime::class.java)
        val updatedAt = row.get("updated_at", LocalDateTime::class.java)
        //val questionRows = row.get("questions", Array::class.java)
        val questions = mutableListOf<Question>()
        /*if (questionRows != null) {
            for (questionRow in questionRows) {
                val question = questionRow
                questions.add(question)
            }
        }*/

        return Quiz(
            id = quizId,
            lessonId = lessonId,
            createdAt = createdAt,
            updatedAt = updatedAt,
            questions = questions
        )
    }
}
