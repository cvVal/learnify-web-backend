package com.demo.learnify.data.mappers

import com.demo.learnify.data.models.Enrollment
import io.r2dbc.spi.Row
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.function.BiFunction

@Component
class EnrollmentDataMapper : BiFunction<Row, Any, Enrollment> {
    override fun apply(row: Row, o: Any): Enrollment {
        return Enrollment(
            id = row.get("enrollment_id", Number::class.java)!!.toInt(),
            courseId = row.get("course_id", Number::class.java)!!.toInt(),
            studentId = row.get("student_id", Number::class.java)!!.toInt(),
            enrollmentDate = row.get("enrollment_date", LocalDateTime::class.java)!!,
            certificateUrl = row.get("certificate_url", String::class.java),
            progress = row.get("progress_percent", Number::class.java)!!.toInt(),
            completed = row.get("is_completed", String::class.java) ?: ""
        )
    }
}
