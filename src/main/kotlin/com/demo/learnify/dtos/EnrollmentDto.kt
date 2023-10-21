package com.demo.learnify.dtos

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class EnrollmentDto(
    val id: Int,
    val courseId: Int,
    val studentId: Int,
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    val enrollmentDate: LocalDateTime = LocalDateTime.now(),
    val certificateUrl: String?,
    val progress: Int?,
    val completed: String?
)
