package com.demo.learnify.services

import com.demo.learnify.dtos.QuestionDto
import com.demo.learnify.dtos.QuizDto
import org.springframework.security.access.prepost.PreAuthorize
import reactor.core.publisher.Mono

interface QuizService {

    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    fun createQuiz(quizDto: QuizDto): Mono<QuizDto>

    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    fun updateQuiz(questionDto: QuestionDto): Mono<QuestionDto>

    fun getQuiz(id: Long): Mono<QuizDto>

    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    fun deleteQuiz(id: Long): Mono<Void>
}
