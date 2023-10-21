package com.demo.learnify.routers

import com.demo.learnify.handlers.LessonHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Configuration
class LessonRouter {

    @Bean
    fun lessonRoutes(lessonHandler: LessonHandler): RouterFunction<ServerResponse> {
        return router {
            POST("/lessons", lessonHandler::createLesson)
            PUT("/lessons", lessonHandler::updateLesson)
            GET("/lessons/course/{courseId}", lessonHandler::getAllLessons)
            GET("/lessons/{lessonId}", lessonHandler::getLesson)
            DELETE("/lessons/{lessonId}", lessonHandler::deleteLesson)
        }
    }
}
