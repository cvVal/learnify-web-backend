package com.demo.learnify.data.mappers

import com.demo.learnify.data.models.Course
import io.r2dbc.spi.Row
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.function.BiFunction

@Component
class CourseDataMapper : BiFunction<Row, Any, Course> {
    override fun apply(row: Row, o: Any): Course {
        return Course(
            id = row.get("id", Number::class.java)!!.toInt(),
            title = row.get("title", String::class.java) ?: "",
            description = row.get("description", String::class.java) ?: "",
            price = row.get("price", Number::class.java)!!.toDouble(),
            duration = row.get("duration", Number::class.java)?.toInt(),
            category = row.get("category", String::class.java),
            tags = row.get("tags", Array::class.java)?.map { it.toString() }?.toSet() ?: emptySet(),
            level = row.get("level", String::class.java),
            instructorId = row.get("instructor_id", Number::class.java)!!.toInt(),
            lessonIds = row.get("lesson_ids", Array::class.java)?.map { it.toString() }?.toList() ?: emptyList(),
            preRequisites = row.get("pre_requisites", String::class.java),
            imageUrl = row.get("image_url", String::class.java),
            published = row.get("is_published", String::class.java) ?: "",
            createdAt = row.get("created_at", LocalDateTime::class.java),
            updatedAt = row.get("updated_at", LocalDateTime::class.java),
            reviews = null
        )
    }
}
