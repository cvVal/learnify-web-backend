package com.demo.learnify.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfig(
    private val customUserDetailsService: CustomUserDetailsService
) {

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .httpBasic()
            .and()
            .csrf().disable()
            .build()
    }
    // How to manage these?
    /*
    GET /lessons/course/{courseId}, /lessons/{lessonId}, /reviews/{reviewId}/user/{userId}, /quizzes/{quizId},
        /enrollments/student/{studentId}, /additional-resources/{resourceId}
     */
    /*
    DELETE /enrollments/{enrollmentId}
    */

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationManager(passwordEncoder: PasswordEncoder): ReactiveAuthenticationManager =
        UserDetailsRepositoryReactiveAuthenticationManager(customUserDetailsService)
            .apply { setPasswordEncoder(passwordEncoder) }

//    companion object {
//        private const val ADMIN = "ADMIN"
//        private const val STUDENT = "STUDENT"
//        private const val INSTRUCTOR = "INSTRUCTOR"
//    }
}
