package com.demo.learnify.data.models

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("quizzes")
data class Quiz(
    @Id val id: Int,
    @Column("lesson_id") val lessonId: Int,
    @Column("created_at") @CreatedDate val createdAt: LocalDateTime? = null,
    @Column("updated_at") @LastModifiedDate val updatedAt: LocalDateTime? = null,
    @Transient val questions: List<Question>?
)
