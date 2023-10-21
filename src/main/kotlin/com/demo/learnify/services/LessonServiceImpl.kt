package com.demo.learnify.services

import com.demo.learnify.data.models.Lesson
import com.demo.learnify.data.repositories.CourseRepository
import com.demo.learnify.data.repositories.LessonRepository
import com.demo.learnify.dtos.LessonDto
import com.demo.learnify.errorhandler.exceptions.NotFoundException
import com.demo.learnify.mappers.LessonMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class LessonServiceImpl(
    private val lessonRepository: LessonRepository,
    private val courseRepository: CourseRepository,
    private val lessonMapper: LessonMapper
) : LessonService {

    @Transactional
    override fun createLesson(lessonRequest: LessonDto): Mono<LessonDto> =
        lessonMapper.convertToLesson(lessonRequest).toMono()
            .flatMap { lessonRepository.saveLesson(it) }
            .flatMap { lesson ->
                courseRepository.findById(lesson.courseId.toLong())
                    .switchIfEmpty(Mono.error(NotFoundException("Course not found")))
                    .map { course ->
                        val lessonIds = course.lessonIds?.toMutableList()
                        lessonIds?.add(lesson.id.toString())
                        course.copy(lessonIds = lessonIds)
                    }
                    .flatMap { updatedCourse ->
                        courseRepository.updateCourse(updatedCourse)
                            .thenReturn(lesson)
                    }
            }
            .map { lessonMapper.convertToLessonDto(it) }

    @Transactional
    override fun updateLesson(lessonRequest: LessonDto): Mono<LessonDto> =
        lessonRepository.findById(lessonRequest.id!!.toLong())
            .switchIfEmpty(Mono.error(NotFoundException("Lesson not found")))
            .flatMap { currentLesson ->
                val updatedLesson = mergeDtoWithLesson(lessonRequest, currentLesson)
                lessonRepository.updateLesson(updatedLesson)
            }
            .map { lessonMapper.convertToLessonDto(it) }

    override fun getAllLessons(courseId: Long): Mono<List<LessonDto>> =
        lessonRepository.findAllLessonsByCourseId(courseId)
            .switchIfEmpty(Mono.error(NotFoundException("Lessons not found")))
            .map { lessonMapper.convertToLessonDto(it) }
            .collectList()

    override fun getLesson(id: Long): Mono<LessonDto> =
        lessonRepository.findById(id)
            .switchIfEmpty(Mono.error(NotFoundException("Lesson not found")))
            .map { lessonMapper.convertToLessonDto(it) }

    override fun deleteLesson(id: Long): Mono<Void> =
        lessonRepository.deleteById(id)

    private fun mergeDtoWithLesson(lessonRequest: LessonDto, lesson: Lesson) =
        lesson.copy(
            title = lessonRequest.title ?: lesson.title,
            description = lessonRequest.description ?: lesson.description,
            duration = lessonRequest.duration ?: lesson.duration,
            status = lessonRequest.status?.name ?: lesson.status,
            contentUrl = lessonRequest.contentUrl ?: lesson.contentUrl,
            order = lessonRequest.order ?: lesson.order
        )
}
