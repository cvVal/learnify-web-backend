package com.demo.learnify.routers

import com.demo.learnify.handlers.AdditionalResourcesHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Configuration
class AdditionalResourcesRouter {

    @Bean
    fun resourcesRouter(handler: AdditionalResourcesHandler): RouterFunction<ServerResponse> {
        return router {
            POST("/additional-resources", handler::addAdditionalResources)
            PUT("/additional-resources", handler::updateAdditionalResources)
            GET("/additional-resources/{resourceId}", handler::getAdditionalResources)
            DELETE("/additional-resources/{resourceId}", handler::deleteAdditionalResources)
        }
    }
}
