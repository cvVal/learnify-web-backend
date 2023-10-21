package com.demo.learnify.routers

import com.demo.learnify.handlers.QuizHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Configuration
class QuizRouter {

    @Bean
    fun quizRoutes(quizHandler: QuizHandler): RouterFunction<ServerResponse> {
        return router {
            POST("/quizzes", quizHandler::createQuiz)
            PUT("/quizzes", quizHandler::updateQuiz)
            GET("/quizzes/{quizId}", quizHandler::getQuiz)
            DELETE("/quizzes/{quizId}", quizHandler::deleteQuiz)
        }
    }
}
