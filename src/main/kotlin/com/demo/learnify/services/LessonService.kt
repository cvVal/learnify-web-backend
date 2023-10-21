package com.demo.learnify.services

import com.demo.learnify.dtos.LessonDto
import org.springframework.security.access.prepost.PreAuthorize
import reactor.core.publisher.Mono

interface LessonService {

    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    fun createLesson(lessonRequest: LessonDto): Mono<LessonDto>

    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    fun updateLesson(lessonRequest: LessonDto): Mono<LessonDto>

    fun getAllLessons(courseId: Long): Mono<List<LessonDto>>
    fun getLesson(id: Long): Mono<LessonDto>

    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    fun deleteLesson(id: Long): Mono<Void>
}
