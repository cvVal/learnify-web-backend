package com.demo.learnify.data.repositories

import com.demo.learnify.data.mappers.ReviewDataMapper
import com.demo.learnify.data.models.Review
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.r2dbc.core.DatabaseClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant

interface ReviewRepository : ReactiveCrudRepository<Review, Long>, CustomReviewRepository

interface CustomReviewRepository {
    fun findAllReviewsByCourseId(courseId: Long): Flux<Review>
    fun findByReviewId(reviewId: Long): Mono<Review>
    fun updateReview(review: Review): Mono<Review>
}

class CustomReviewRepositoryImpl(
    private val databaseClient: DatabaseClient,
    private val mapper: ReviewDataMapper
) : CustomReviewRepository {
    override fun findAllReviewsByCourseId(courseId: Long): Flux<Review> =
        databaseClient.sql(SELECT_ALL_REVIEWS_BY_COURSE_ID)
            .bind("courseId", courseId)
            .map(mapper::apply)
            .all()

    override fun findByReviewId(reviewId: Long): Mono<Review> =
        databaseClient.sql(SELECT_REVIEW_BY_USER_ID)
            .bind("id", reviewId)
            .map(mapper::apply)
            .one()

    override fun updateReview(review: Review): Mono<Review> =
        databaseClient.sql(UPDATE_REVIEW)
            .bind("id", review.id)
            .bind("rating", review.rating)
            .bind("comment", review.comment)
            .bind("updatedAt", Instant.now())
            .fetch()
            .rowsUpdated()
            .flatMap { if (it > 0) Mono.just(review) else Mono.empty() }


    companion object {
        const val SELECT_ALL_REVIEWS_BY_COURSE_ID = """
            SELECT r.id, r.course_id, r.user_id, r.rating, r.comment, r.created_at, r.updated_at,
                u.first_name, u.last_name
            FROM reviews r
            INNER JOIN users u ON r.user_id = u.id
            WHERE r.course_id = :courseId;
        """
        const val SELECT_REVIEW_BY_USER_ID = """
            SELECT r.id, r.course_id, r.user_id, r.rating, r.comment, r.created_at, r.updated_at, 
                u.first_name, u.last_name, u.email
            FROM reviews r 
            INNER JOIN users u ON r.user_id = u.id 
            WHERE r.id = :id
        """
        const val UPDATE_REVIEW = """
            UPDATE reviews SET
                rating = :rating,
                comment = :comment,
                updated_at = :updatedAt
            WHERE id = :id
        """
    }
}
