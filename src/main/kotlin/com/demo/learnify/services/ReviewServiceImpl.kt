package com.demo.learnify.services

import com.demo.learnify.data.models.Review
import com.demo.learnify.data.repositories.ReviewRepository
import com.demo.learnify.dtos.ReviewDto
import com.demo.learnify.errorhandler.exceptions.ForbiddenException
import com.demo.learnify.errorhandler.exceptions.NotFoundException
import com.demo.learnify.mappers.ReviewMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDateTime

@Service
class ReviewServiceImpl(
    private val reviewRepository: ReviewRepository,
    private val reviewMapper: ReviewMapper
) : ReviewService {

    @Transactional
    override fun createReview(reviewRequest: ReviewDto): Mono<ReviewDto> =
        reviewMapper.convertToReview(reviewRequest).toMono()
            .flatMap { reviewRepository.save(it) }
            .map { reviewMapper.convertToDto(it) }

    @Transactional
    override fun updateReview(reviewRequest: ReviewDto, name: String): Mono<ReviewDto> =
        reviewRepository.findByReviewId(reviewRequest.id!!.toLong())
            .switchIfEmpty(Mono.error(NotFoundException("Review not found")))
            .filter { it.email == name }
            .switchIfEmpty(Mono.error(ForbiddenException("This user cannot update the review")))
            .flatMap { existingReview ->
                val updatedReview = mergeDtoWithReview(reviewRequest, existingReview)
                reviewRepository.updateReview(updatedReview)
            }
            .map { reviewMapper.convertToDto(it) }

    override fun getReview(id: Long): Mono<ReviewDto> =
        reviewRepository.findByReviewId(id)
            .switchIfEmpty(Mono.error(NotFoundException("Review not found")))
            .map { reviewMapper.convertToDto(it) }

    override fun deleteReview(id: Long): Mono<Void> =
        reviewRepository.deleteById(id)

    private fun mergeDtoWithReview(reviewDto: ReviewDto, review: Review): Review {
        return review.copy(
            rating = reviewDto.rating ?: review.rating,
            comment = reviewDto.comment ?: review.comment,
            updatedAt = LocalDateTime.now()
        )
    }
}
