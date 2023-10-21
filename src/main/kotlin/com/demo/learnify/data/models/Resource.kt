package com.demo.learnify.data.models

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("resources")
data class Resource(
    @Id val id: Int,
    @Column("lesson_id") val lessonId: Int,
    @Column("resource_url") val resourceUrl: String,
    @CreatedDate val createdAt: LocalDateTime? = null,
    @LastModifiedDate val updatedAt: LocalDateTime? = null,
    val name: String
)
