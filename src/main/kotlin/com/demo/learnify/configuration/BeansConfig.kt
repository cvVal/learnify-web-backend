package com.demo.learnify.configuration

import org.springframework.boot.autoconfigure.web.WebProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import java.time.Duration


@Configuration
class BeansConfig {

    @Bean
    fun resources(): WebProperties.Resources {
        return WebProperties.Resources()
    }

    @Bean
    fun corsWebFilter(): CorsWebFilter {
        val corsConfig = CorsConfiguration()
        corsConfig.allowedOrigins = listOf("*") // or use "*" for any origin
        corsConfig.allowedMethods = listOf("*") // or specify specific HTTP methods
        corsConfig.allowedHeaders = listOf("*") // or specify specific headers
        corsConfig.setMaxAge(Duration.ofHours(1)) // set max age for pre-flight requests
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", corsConfig) // apply CORS configuration to all paths
        return CorsWebFilter(source)
    }
}