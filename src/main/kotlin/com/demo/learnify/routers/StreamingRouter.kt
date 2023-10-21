package com.demo.learnify.routers

import com.demo.learnify.handlers.StreamingHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Configuration
class StreamingRouter {

    @Bean
    fun streamingRoutes(streamingHandler: StreamingHandler): RouterFunction<ServerResponse> {
        return router {
            GET("streaming/{title}", streamingHandler::getVideo)
        }
    }
}