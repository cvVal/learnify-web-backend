package com.demo.learnify.data.models

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Duration
import java.time.LocalDateTime

@Table("lessons")
data class Lesson(
    @Id val id: Int,
    val title: String,
    val description: String,
    val duration: Int?,
    val status: String,
    @Column("course_id") val courseId: Int,
    @Column("content_url") val contentUrl: String?,
    @Column("order_number") val order: Int,
    @Column("created_at") @CreatedDate val createdAt: LocalDateTime? = null,
    @Column("updated_at") @LastModifiedDate val updatedAt: LocalDateTime? = null,
    @Transient val quiz: Quiz? = null,
    @Transient val additionalResources: Set<Resource>? = null
)
