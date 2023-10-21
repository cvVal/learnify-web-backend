package com.demo.learnify.data.models

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.time.LocalDateTime

@Table("users")
data class User(
    @Id val id: Int? = null,
    @Column("first_name") val firstName: String,
    @Column("last_name") val lastName: String,
    @Column("profile_picture") val profilePictureUrl: String?,
    val email: String,
    val password: String,
    val roles: Set<String>,
    val bio: String?,
    @CreatedDate val createdAt: LocalDateTime?,
    @LastModifiedDate val updatedAt: LocalDateTime?
)
