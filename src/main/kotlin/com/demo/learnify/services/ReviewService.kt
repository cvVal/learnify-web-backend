package com.demo.learnify.services

import com.demo.learnify.dtos.ReviewDto
import org.springframework.security.access.prepost.PreAuthorize
import reactor.core.publisher.Mono

interface ReviewService {

    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    fun createReview(reviewRequest: ReviewDto): Mono<ReviewDto>

    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR', 'ADMIN')")
    fun updateReview(reviewRequest: ReviewDto, name: String): Mono<ReviewDto>

    fun getReview(id: Long): Mono<ReviewDto>

    @PreAuthorize("hasRole('ADMIN')")
    fun deleteReview(id: Long): Mono<Void>
}
