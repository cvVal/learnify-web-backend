package com.demo.learnify.data.models

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("questions")
data class Question(
    @Id val id: Int,
    @Column("quiz_id") val quizId: Int,
    val question: String,
    val options: List<String>,
    @Column("correct_option") val correctOption: String
)
