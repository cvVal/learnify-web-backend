package com.demo.learnify.dtos

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class ResourceDto(
    val id: Int,
    val lessonId: Int?,
    val resourceUrl: String?,
    val name: String?,
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    val createdAt: LocalDateTime?,
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    val updatedAt: LocalDateTime?
)
