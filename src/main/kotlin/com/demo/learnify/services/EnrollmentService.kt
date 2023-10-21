package com.demo.learnify.services

import com.demo.learnify.dtos.CourseEnrollmentDto
import com.demo.learnify.dtos.EnrollmentDto
import org.springframework.security.access.prepost.PreAuthorize
import reactor.core.publisher.Mono

interface EnrollmentService {

    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR', 'ADMIN')")
    fun createEnrollment(request: EnrollmentDto): Mono<EnrollmentDto>

    fun updateEnrollment(request: EnrollmentDto): Mono<EnrollmentDto>
    fun getAllEnrollmentsByUser(studentId: Long): Mono<List<CourseEnrollmentDto>>

    @PreAuthorize("hasRole('ADMIN')")
    fun deleteEnrollment(id: Long): Mono<Void>
}
