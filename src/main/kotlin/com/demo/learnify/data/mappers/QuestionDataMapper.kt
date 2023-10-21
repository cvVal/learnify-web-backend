package com.demo.learnify.data.mappers

import com.demo.learnify.data.models.Question
import io.r2dbc.spi.Row
import org.springframework.stereotype.Component
import java.util.function.BiFunction

@Component
class QuestionDataMapper : BiFunction<Row, Any, Question> {
    override fun apply(row: Row, o: Any): Question {
        return Question(
            id = row.get("id", Number::class.java)!!.toInt(),
            quizId = row.get("quiz_id", Number::class.java)!!.toInt(),
            question = row.get("question", String::class.java) ?: "",
            options = row.get("options", Array::class.java)?.map { it.toString() }?.toList() ?: emptyList(),
            correctOption = row.get("correct_option", String::class.java) ?: ""
        )
    }
}
