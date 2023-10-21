package com.demo.learnify.dtos

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

data class CreateUserDto(
    val id: Int?,
    @field: NotBlank val firstName: String,
    @field: NotBlank val lastName: String,
    @field: Email val email: String,
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    val password: String,
    val profilePictureUrl: String?,
    val roles: Set<Role>,
    val bio: String?,
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    val createdAt: LocalDateTime? = null,
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    val updatedAt: LocalDateTime? = null,
)

data class UpdateUserDto(
    @field: NotBlank val id: Int,
    @field: NotBlank val firstName: String?,
    @field: NotBlank val lastName: String?,
    val profilePictureUrl: String?,
    val roles: Set<Role>? = null,
    val bio: String?
)

enum class Role {
    ADMIN,
    STUDENT,
    INSTRUCTOR
}
