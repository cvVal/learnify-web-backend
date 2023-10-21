package com.demo.learnify.routers

import com.demo.learnify.handlers.ReviewHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Configuration
class ReviewRouter {

    @Bean
    fun reviewRoutes(reviewHandler: ReviewHandler): RouterFunction<ServerResponse> {
        return router {
            POST("/reviews", reviewHandler::createReview)
            PUT("/reviews", reviewHandler::updateReview)
            GET("/reviews/{reviewId}", reviewHandler::getReview)
            DELETE("/reviews/{reviewId}", reviewHandler::deleteReview)
        }
    }
}
