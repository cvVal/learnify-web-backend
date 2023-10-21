package com.demo.learnify.handlers

import com.demo.learnify.dtos.LessonDto
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
class LessonHandler(
    private val lessonService: LessonService
) {

    fun createLesson(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest.bodyToMono<LessonDto>()
            .flatMap { lessonService.createLesson(it) }
            .flatMap { createdJsonResponse(it) }

    fun updateLesson(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest.bodyToMono<LessonDto>()
            .flatMap { lessonService.updateLesson(it) }
            .flatMap { successJsonResponse(it) }

    fun getAllLessons(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest.toMono()
            .map { serverRequest.pathVariable("courseId") }
            .flatMap { lessonService.getAllLessons(it.toLong()) }
            .flatMap { successJsonResponse(it) }

    fun getLesson(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest.toMono()
            .map { serverRequest.pathVariable("lessonId") }
            .flatMap { lessonService.getLesson(it.toLong()) }
            .flatMap { successJsonResponse(it) }

    fun deleteLesson(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest.toMono()
            .map { serverRequest.pathVariable("lessonId") }
            .flatMap { lessonService.deleteLesson(it.toLong()) }
            .flatMap { ServerResponse.noContent().build() }
}
