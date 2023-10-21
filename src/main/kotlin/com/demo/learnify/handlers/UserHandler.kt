package com.demo.learnify.handlers

import com.demo.learnify.dtos.CreateUserDto
import com.demo.learnify.dtos.UpdateUserDto
import com.demo.learnify.services.UserService
import com.demo.learnify.utils.createdJsonResponse
import com.demo.learnify.utils.successJsonResponse
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class UserHandler(
    private val userService: UserService
) {

    fun createUser(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest.bodyToMono<CreateUserDto>()
            .flatMap { userService.createUser(it) }
            .flatMap { createdJsonResponse(it) }

    fun updateUser(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest.principal()
            .flatMap { principal ->
                serverRequest.bodyToMono<UpdateUserDto>()
                    .flatMap { userService.updateUser(it, principal.name) }
            }
            .flatMap { successJsonResponse(it) }

    fun getAllUsers(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest.toMono()
            .flatMap { userService.getAllUsers() }
            .flatMap { successJsonResponse(it) }

    fun getUser(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest.toMono()
            .map { serverRequest.pathVariable("userId") }
            .flatMap { userService.getUser(it.toLong()) }
            .flatMap { successJsonResponse(it) }

    fun getUserByEmail(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest.toMono()
            .map { serverRequest.pathVariable("userEmail") }
            .flatMap { userService.getUserByEmail(it) }
            .flatMap { successJsonResponse(it) }

    fun deleteUser(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest.toMono()
            .map { serverRequest.pathVariable("userId") }
            .flatMap { userService.deleteUser(it.toLong()) }
            .flatMap { ServerResponse.noContent().build() }
}
