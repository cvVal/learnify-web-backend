package com.demo.learnify.routers

import com.demo.learnify.handlers.UserHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Configuration
class UserRouter {

    @Bean
    fun userRoutes(userHandler: UserHandler): RouterFunction<ServerResponse> {
        return router {
            POST("/users", userHandler::createUser)
            PUT("/users", userHandler::updateUser)
            GET("/users", userHandler::getAllUsers)
            GET("/users/{userId}", userHandler::getUser)
            GET("/users/email/{userEmail}", userHandler::getUserByEmail)
            DELETE("/users/{userId}", userHandler::deleteUser)
        }
    }
}
