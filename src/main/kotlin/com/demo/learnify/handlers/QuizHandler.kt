package com.demo.learnify.handlers

import com.demo.learnify.dtos.QuestionDto
import com.demo.learnify.dtos.QuizDto
import com.demo.learnify.services.QuizService
import com.demo.learnify.utils.createdJsonResponse
import com.demo.learnify.utils.successJsonResponse
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class QuizHandler(
    private val quizService: QuizService
) {

    fun createQuiz(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest.bodyToMono<QuizDto>()
            .flatMap { quizService.createQuiz(it) }
            .flatMap { createdJsonResponse(it) }

    fun updateQuiz(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest.bodyToMono<QuestionDto>()
            .flatMap { quizService.updateQuiz(it) }
            .flatMap { successJsonResponse(it) }

    fun getQuiz(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest.toMono()
            .map { serverRequest.pathVariable("quizId") }
            .flatMap { quizService.getQuiz(it.toLong()) }
            .flatMap { successJsonResponse(it) }

    fun deleteQuiz(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest.toMono()
            .map { serverRequest.pathVariable("quizId") }
            .flatMap { quizService.deleteQuiz(it.toLong()) }
            .flatMap { ServerResponse.noContent().build() }
}
