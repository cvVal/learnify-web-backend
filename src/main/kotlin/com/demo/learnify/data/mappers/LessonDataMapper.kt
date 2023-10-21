package com.demo.learnify.data.mappers

import com.demo.learnify.data.models.Lesson
import io.r2dbc.spi.Row
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.function.BiFunction

@Component
class LessonDataMapper : BiFunction<Row, Any, Lesson> {
    override fun apply(row: Row, o: Any): Lesson {
        return Lesson(
            id = row.get("id", Number::class.java)!!.toInt(),
            title = row.get("title", String::class.java) ?: "",
            description = row.get("description", String::class.java) ?: "",
            duration = row.get("duration", Number::class.java)?.toInt(),
            status = row.get("status", String::class.java) ?: "",
            courseId = row.get("course_id", Number::class.java)!!.toInt(),
            contentUrl = row.get("content_url", String::class.java),
            order = row.get("order_number", Number::class.java)!!.toInt(),
            createdAt = row.get("created_at", LocalDateTime::class.java),
            updatedAt = row.get("updated_at", LocalDateTime::class.java)
        )
    }
}
