package com.demo.learnify.dtos

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class CourseDto(
    val id: Int?,
    val title: String?,
    val description: String?,
    val price: Double?,
    val duration: Int?,
    val category: String?,
    val tags: Set<String>?,
    val level: String?,
    val instructorId: Int?,
    val lessonIds: List<String>?,
    val preRequisites: String?,
    val imageUrl: String?,
    val published: String?,
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    val createdAt: LocalDateTime?,
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    val updatedAt: LocalDateTime?,
    val reviews: List<ReviewDto>?
)
