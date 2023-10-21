package com.demo.learnify.data.models

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("courses")
data class Course(
    @Id val id: Int,
    val title: String,
    val description: String,
    val price: Double? = null,
    val duration: Int? = null,
    val category: String? = null,
    val tags: Set<String>? = null,
    val level: String? = null,
    @Column("instructor_id") val instructorId: Int,
    @Column("lesson_ids") val lessonIds: List<String>? = null,
    @Column("pre_requisites") val preRequisites: String? = null,
    @Column("image_url") val imageUrl: String? = null,
    @Column("is_published") val published: String,
    @CreatedDate val createdAt: LocalDateTime? = null,
    @LastModifiedDate val updatedAt: LocalDateTime? = null,
    @Transient val reviews: List<Review>? = null
)
