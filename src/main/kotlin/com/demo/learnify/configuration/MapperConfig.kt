package com.demo.learnify.configuration

import com.demo.learnify.mappers.*
import org.mapstruct.factory.Mappers
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MapperConfig {

    @Bean
    fun userMapper(): UserMapper = Mappers.getMapper(UserMapper::class.java)

    @Bean
    fun courseMapper(): CourseMapper = Mappers.getMapper(CourseMapper::class.java)

    @Bean
    fun reviewMapper(): ReviewMapper = Mappers.getMapper(ReviewMapper::class.java)

    @Bean
    fun lessonMapper(): LessonMapper = Mappers.getMapper(LessonMapper::class.java)

    @Bean
    fun enrollmentMapper(): EnrollmentMapper = Mappers.getMapper(EnrollmentMapper::class.java)
}
