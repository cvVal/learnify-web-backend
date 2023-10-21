package com.demo.learnify.data.mappers

import com.demo.learnify.data.models.Review
import io.r2dbc.spi.Row
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.function.BiFunction

@Component
class ReviewDataMapper : BiFunction<Row, Any, Review> {
    override fun apply(row: Row, o: Any): Review {
        val email = if (row.metadata.contains("email")) row.get("email", String::class.java) else null
        return Review(
            id = row.get("id", Number::class.java)!!.toInt(),
            courseId = row.get("course_id", Number::class.java)!!.toInt(),
            userId = row.get("user_id", Number::class.java)!!.toInt(),
            firstName = row.get("first_name", String::class.java) ?: "",
            lastName = row.get("last_name", String::class.java) ?: "",
            email = email,
            rating = row.get("rating", Number::class.java)!!.toInt(),
            comment = row.get("comment", String::class.java) ?: "",
            createdAt = row.get("created_at", LocalDateTime::class.java),
            updatedAt = row.get("updated_at", LocalDateTime::class.java)
        )
    }
}
