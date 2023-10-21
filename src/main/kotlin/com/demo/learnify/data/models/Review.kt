package com.demo.learnify.data.models

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("reviews")
data class Review(
    @Id val id: Int,
    @Column("first_name") val firstName: String?,
    @Column("last_name") val lastName: String?,
    @Column("email") val email: String?,
    @Column("course_id") val courseId: Int,
    @Column("user_id") val userId: Int,
    val rating: Int,
    val comment: String,
    @CreatedDate val createdAt: LocalDateTime?,
    @LastModifiedDate val updatedAt: LocalDateTime?
)
