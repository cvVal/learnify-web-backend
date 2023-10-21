package com.demo.learnify.routers

import com.demo.learnify.handlers.CourseHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Configuration
class CourseRouter {

    @Bean
    fun courseRoutes(courseHandler: CourseHandler): RouterFunction<ServerResponse> {
        return router {
            POST("/courses", courseHandler::createCourse)
            PUT("/courses", courseHandler::updateCourse)
            GET("/courses", courseHandler::getAllCourses)
            GET("/courses/id/{courseId}", courseHandler::getCourse)
            DELETE("/courses/{courseId}", courseHandler::deleteCourse)
            GET("/courses/top-rated", courseHandler::getTopRatedCourses)
            GET("/courses/title-containing/{title}", courseHandler::getCoursesByTitleContaining)
            GET("/courses/instructor/{instructorId}", courseHandler::getCoursesByInstructorId)
            GET("/courses/category/{category}", courseHandler::getCoursesByCategory)
            GET("/courses/level/{level}", courseHandler::getCoursesByLevel)
        }
    }
}
