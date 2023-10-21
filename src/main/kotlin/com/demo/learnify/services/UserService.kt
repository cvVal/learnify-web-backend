package com.demo.learnify.services

import com.demo.learnify.dtos.CreateUserDto
import com.demo.learnify.dtos.UpdateUserDto
import org.springframework.security.access.prepost.PreAuthorize
import reactor.core.publisher.Mono

interface UserService {

    fun createUser(userRequest: CreateUserDto): Mono<CreateUserDto>

    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR', 'ADMIN')")
    fun updateUser(userRequest: UpdateUserDto, name: String): Mono<CreateUserDto>

    fun getAllUsers(): Mono<List<CreateUserDto>>
    fun getUser(id: Long): Mono<CreateUserDto>
    fun getUserByEmail(email: String): Mono<CreateUserDto>

    @PreAuthorize("hasRole('ADMIN')")
    fun deleteUser(id: Long): Mono<Void>
}
