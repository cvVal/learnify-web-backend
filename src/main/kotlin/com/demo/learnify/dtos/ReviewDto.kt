package com.demo.learnify.dtos

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ReviewDto(
    val id: Int?,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val courseId: Int,
    val userId: Int,
    val rating: Int?,
    val comment: String?,
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    val createdAt: LocalDateTime?,
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    val updatedAt: LocalDateTime?,
)
