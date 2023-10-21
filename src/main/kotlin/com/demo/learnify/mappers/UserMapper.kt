package com.demo.learnify.mappers

import com.demo.learnify.data.models.User
import com.demo.learnify.dtos.CreateUserDto
import com.demo.learnify.dtos.Role
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Named

@Mapper
interface UserMapper {

    @Mapping(target = "roles", source = "roles", qualifiedByName = ["toRoleNames"])
    fun convertToUser(createUserDto: CreateUserDto): User

    @Mapping(target = "roles", source = "roles", qualifiedByName = ["toUserRoles"])
    fun convertToDto(user: User): CreateUserDto

    @Named("toRoleNames")
    fun toRoleNames(roles: Set<Role>): Set<String> = roles.map { it.name }.toSet()

    @Named("toUserRoles")
    fun toUserRoles(roles: Set<String>): Set<Role> = roles.map { role ->
        when (role.uppercase()) {
            "ADMIN" -> Role.ADMIN
            "STUDENT" -> Role.STUDENT
            "INSTRUCTOR" -> Role.INSTRUCTOR
            else -> throw IllegalArgumentException("Invalid role name: $role")
        }
    }.toSet()
}
