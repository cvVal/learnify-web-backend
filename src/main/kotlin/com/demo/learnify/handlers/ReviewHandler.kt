package com.demo.learnify.handlers

import com.demo.learnify.dtos.ReviewDto
import com.demo.learnify.services.ReviewService
import com.demo.learnify.utils.createdJsonResponse
import com.demo.learnify.utils.successJsonResponse
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class ReviewHandler(
    private val reviewService: ReviewService
) {

    fun createReview(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest.bodyToMono<ReviewDto>()
            .flatMap { reviewService.createReview(it) }
            .flatMap { createdJsonResponse(it) }

    fun updateReview(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest.principal()
            .flatMap { principal ->
                serverRequest.bodyToMono<ReviewDto>()
                    .flatMap { reviewService.updateReview(it, principal.name) }
            }
            .flatMap { successJsonResponse(it) }

    fun getReview(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest.pathVariable("reviewId").toMono()
            .flatMap { reviewService.getReview(it.toLong()) }
            .flatMap { successJsonResponse(it) }

    fun deleteReview(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest.pathVariable("reviewId").toMono()
            .flatMap { reviewService.deleteReview(it.toLong()) }
            .flatMap { ServerResponse.noContent().build() }
}
