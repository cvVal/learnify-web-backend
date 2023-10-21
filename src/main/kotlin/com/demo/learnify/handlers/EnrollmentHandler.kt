package com.demo.learnify.handlers

import com.demo.learnify.dtos.EnrollmentDto
import com.demo.learnify.dtos.LessonDto
import com.demo.learnify.services.EnrollmentService
import com.demo.learnify.services.LessonService
import com.demo.learnify.utils.createdJsonResponse
import com.demo.learnify.utils.successJsonResponse
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class EnrollmentHandler(
    private val enrollmentService: EnrollmentService
) {

    fun createEnrollment(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest.bodyToMono<EnrollmentDto>()
            .flatMap { enrollmentService.createEnrollment(it) }
            .flatMap { createdJsonResponse(it) }

    fun updateEnrollment(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest.bodyToMono<EnrollmentDto>()
            .flatMap { enrollmentService.updateEnrollment(it) }
            .flatMap { successJsonResponse(it) }

    fun getAllEnrollments(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest.toMono()
            .map { serverRequest.pathVariable("studentId") }
            .flatMap { enrollmentService.getAllEnrollmentsByUser(it.toLong()) }
            .flatMap { successJsonResponse(it) }

    fun deleteEnrollment(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest.toMono()
            .map { serverRequest.pathVariable("enrollmentId") }
            .flatMap { enrollmentService.deleteEnrollment(it.toLong()) }
            .flatMap { ServerResponse.noContent().build() }
}
