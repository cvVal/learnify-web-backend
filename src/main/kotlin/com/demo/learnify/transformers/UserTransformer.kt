package com.demo.learnify.transformers

import com.demo.learnify.data.models.User
import com.demo.learnify.dtos.CreateUserDto
import com.demo.learnify.dtos.Role
import com.demo.learnify.dtos.UpdateUserDto
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class UserTransformer {
    fun convertToUser(createUserDto: CreateUserDto) = User(
        firstName = createUserDto.firstName,
        lastName = createUserDto.lastName,
        profilePictureUrl = createUserDto.profilePictureUrl,
        email = createUserDto.email,
        password = createUserDto.password,
        roles = createUserDto.roles.map { it.name }.toSet(),
        bio = createUserDto.bio,
        createdAt = createUserDto.createdAt,
        updatedAt = createUserDto.updatedAt
    )

    fun convertToDto(user: User) = CreateUserDto(
        id = user.id ?: 0,
        firstName = user.firstName,
        lastName = user.lastName,
        profilePictureUrl = user.profilePictureUrl,
        email = user.email,
        password = user.password,
        roles = user.roles.map { toUserRole(it) }.toSet(),
        bio = user.bio,
        createdAt = user.createdAt,
        updatedAt = user.updatedAt
    )

    fun toUserRole(roleName: String): Role {
        return when (roleName.uppercase()) {
            "ADMIN" -> Role.ADMIN
            "STUDENT" -> Role.STUDENT
            "INSTRUCTOR" -> Role.INSTRUCTOR
            else -> throw IllegalArgumentException("Invalid role name: $roleName")
        }
    }

    fun mergeUserDtoWithUser(userDto: UpdateUserDto, user: User): User {
        return user.copy(
            firstName = userDto.firstName ?: user.firstName,
            lastName = userDto.lastName ?: user.lastName,
            profilePictureUrl = userDto.profilePictureUrl ?: user.profilePictureUrl,
            roles = userDto.roles?.map { it.name }?.toSet() ?: user.roles,
            bio = userDto.bio ?: user.bio,
            updatedAt = LocalDateTime.now()
        )
    }
}
