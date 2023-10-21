package com.demo.learnify.data.models

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("enrollments")
data class Enrollment(
    @Id val id: Int,
    @Column("course_id") val courseId: Int,
    @Column("student_id") val studentId: Int,
    @Column("enrollment_date") val enrollmentDate: LocalDateTime,
    @Column("certificate_url") val certificateUrl: String?,
    @Column("progress_percent") val progress: Int,
    @Column("is_completed") val completed: String
)
