package com.demo.learnify.dtos

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class QuizDto(
    val id: Int?,
    val lessonId: Int,
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    val createdAt: LocalDateTime? = null,
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    val updatedAt: LocalDateTime? = null,
    val questions: List<QuestionDto>?
)

data class QuestionDto(
    val id: Int?,
    val quizId: Int?,
    val question: String?,
    val options: List<String>?,
    val correctOption: String?
)
