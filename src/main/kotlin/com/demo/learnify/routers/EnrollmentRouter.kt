package com.demo.learnify.routers

import com.demo.learnify.handlers.EnrollmentHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Configuration
class EnrollmentRouter {

    @Bean
    fun enrollmentRoutes(enrollmentHandler: EnrollmentHandler): RouterFunction<ServerResponse> {
        return router {
            POST("/enrollments", enrollmentHandler::createEnrollment)
            PUT("/enrollments", enrollmentHandler::updateEnrollment)
            GET("/enrollments/student/{studentId}", enrollmentHandler::getAllEnrollments)
            DELETE("/enrollments/{enrollmentId}", enrollmentHandler::deleteEnrollment)
        }
    }
}
