package com.demo.learnify.services

import com.demo.learnify.dtos.CourseDto
import com.demo.learnify.dtos.CreateUserDto
import com.demo.learnify.dtos.UpdateUserDto
import org.springframework.security.access.prepost.PreAuthorize
import reactor.core.publisher.Mono

interface CourseService {

    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    fun createCourse(courseRequest: CourseDto): Mono<CourseDto>

    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    fun updateCourse(courseRequest: CourseDto): Mono<CourseDto>

    fun getAllCourses(): Mono<List<CourseDto>>
    fun getCourse(id: Int): Mono<CourseDto>
    fun getTopRatedCourses(limit: Int = 100): Mono<List<CourseDto>>
    fun getCoursesByTitleContaining(filter: String): Mono<List<CourseDto>>
    fun getCoursesByInstructorId(id: Int): Mono<List<CourseDto>>
    fun getCoursesByCategory(category: String): Mono<List<CourseDto>>
    fun getCoursesByLevel(level: String): Mono<List<CourseDto>>

    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    fun deleteCourse(id: Int): Mono<Void>
}
