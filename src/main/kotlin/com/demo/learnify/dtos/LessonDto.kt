package com.demo.learnify.dtos

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class LessonDto(
    val id: Int?,
    val title: String?,
    val description: String?,
    val duration: Int?,
    val status: LessonStatus?,
    val courseId: Int,
    val contentUrl: String?,
    val order: Int?,
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    val createdAt: LocalDateTime? = null,
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    val updatedAt: LocalDateTime? = null,
    val quiz: QuizDto?,
    val additionalResources: Set<ResourceDto>?
) {
    enum class LessonStatus {
        NOT_STARTED,
        IN_PROGRESS,
        COMPLETED
    }
}
