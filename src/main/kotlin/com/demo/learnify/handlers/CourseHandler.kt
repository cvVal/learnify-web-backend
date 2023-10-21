package com.demo.learnify.handlers

import com.demo.learnify.dtos.CourseDto
import com.demo.learnify.services.CourseService
import com.demo.learnify.utils.createdJsonResponse
import com.demo.learnify.utils.successJsonResponse
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class CourseHandler(
    private val courseService: CourseService
) {

    fun createCourse(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest.bodyToMono<CourseDto>()
            .flatMap { courseService.createCourse(it) }
            .flatMap { createdJsonResponse(it) }

    fun updateCourse(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest.bodyToMono<CourseDto>()
            .flatMap { courseService.updateCourse(it) }
            .flatMap { successJsonResponse(it) }

    fun getAllCourses(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest.toMono()
            .flatMap { courseService.getAllCourses() }
            .flatMap { successJsonResponse(it) }

    fun getCourse(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest.toMono()
            .map { serverRequest.pathVariable("courseId") }
            .flatMap { courseService.getCourse(it.toInt()) }
            .flatMap { successJsonResponse(it) }

    fun deleteCourse(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest.toMono()
            .map { serverRequest.pathVariable("courseId") }
            .flatMap { courseService.deleteCourse(it.toInt()) }
            .flatMap { ServerResponse.noContent().build() }

    fun getTopRatedCourses(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest.toMono()
            .flatMap { courseService.getTopRatedCourses() }
            .flatMap { successJsonResponse(it) }

    fun getCoursesByTitleContaining(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest.toMono()
            .map { serverRequest.pathVariable("title") }
            .flatMap { courseService.getCoursesByTitleContaining(it) }
            .flatMap { successJsonResponse(it) }

    fun getCoursesByInstructorId(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest.toMono()
            .map { serverRequest.pathVariable("instructorId") }
            .flatMap { courseService.getCoursesByInstructorId(it.toInt()) }
            .flatMap { successJsonResponse(it) }

    fun getCoursesByCategory(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest.toMono()
            .map { serverRequest.pathVariable("category") }
            .flatMap { courseService.getCoursesByCategory(it) }
            .flatMap { successJsonResponse(it) }

    fun getCoursesByLevel(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest.toMono()
            .map { serverRequest.pathVariable("level") }
            .flatMap { courseService.getCoursesByLevel(it) }
            .flatMap { successJsonResponse(it) }
}
