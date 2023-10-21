package com.demo.learnify.services

import com.demo.learnify.data.models.Course
import com.demo.learnify.data.repositories.CourseRepository
import com.demo.learnify.data.repositories.ReviewRepository
import com.demo.learnify.dtos.CourseDto
import com.demo.learnify.errorhandler.exceptions.ConflictException
import com.demo.learnify.errorhandler.exceptions.NotFoundException
import com.demo.learnify.mappers.CourseMapper
import org.reactivestreams.Publisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDateTime

@Service
class CourseServiceImpl(
    private val courseRepository: CourseRepository,
    private val reviewRepository: ReviewRepository,
    private val courseMapper: CourseMapper
) : CourseService {

    @Transactional
    override fun createCourse(courseRequest: CourseDto): Mono<CourseDto> =
        courseMapper.convertToCourse(courseRequest).toMono()
            .flatMap { courseRepository.saveCourse(it) }
            .map { courseMapper.convertToDto(it) }

    @Transactional
    override fun updateCourse(courseRequest: CourseDto): Mono<CourseDto> =
        courseRepository.findById(courseRequest.id!!.toLong())
            .switchIfEmpty(Mono.error(NotFoundException("Course not found")))
            .flatMap { existingCourse ->
                val updatedCourse = mergeDtoWithCourse(courseRequest, existingCourse)
                courseRepository.updateCourse(updatedCourse)
            }
            .map { courseMapper.convertToDto(it) }

    override fun getAllCourses(): Mono<List<CourseDto>> =
        getCoursesWithReviews {
            courseRepository.findAllCourses()
                .switchIfEmpty(Mono.error(NotFoundException("There are no courses available")))
        }

    override fun getCourse(id: Int): Mono<CourseDto> =
        getCoursesWithReviews {
            courseRepository.findById(id.toLong())
                .switchIfEmpty(Mono.error(NotFoundException("Course not found")))
        }
            .flatMap { courses ->
                courses.firstOrNull()?.let {
                    Mono.just(it)
                } ?: Mono.empty()
            }

    override fun getTopRatedCourses(limit: Int): Mono<List<CourseDto>> =
        getCoursesWithReviews { courseRepository.findTopCoursesByRating() }

    override fun getCoursesByTitleContaining(filter: String): Mono<List<CourseDto>> =
        getCoursesWithReviews {
            courseRepository.findAllByTitleContaining(filter)
                .switchIfEmpty(Mono.error(NotFoundException("Course with title $filter not found")))
        }

    override fun getCoursesByInstructorId(id: Int): Mono<List<CourseDto>> =
        getCoursesWithReviews {
            courseRepository.findAllByInstructorId(id)
                .switchIfEmpty(Mono.error(NotFoundException("Course not found")))
        }

    override fun getCoursesByCategory(category: String): Mono<List<CourseDto>> =
        getCoursesWithReviews {
            courseRepository.findAllByCategory(category)
                .switchIfEmpty(Mono.error(NotFoundException("Course with category $category not found")))
        }

    override fun getCoursesByLevel(level: String): Mono<List<CourseDto>> =
        getCoursesWithReviews {
            courseRepository.findAllByLevel(level)
                .switchIfEmpty(Mono.error(NotFoundException("Course with $level level not found")))
        }

    override fun deleteCourse(id: Int): Mono<Void> =
        courseRepository.findById(id.toLong())
            .filter { it.published == "false" }
            .switchIfEmpty(Mono.error(ConflictException("A published course cannot be deleted")))
            .flatMap { courseRepository.deleteById(id.toLong()) }

    private fun getCoursesWithReviews(fetcher: () -> Publisher<Course>): Mono<List<CourseDto>> =
        Flux.from(fetcher.invoke())
            .flatMap { course ->
                reviewRepository.findAllReviewsByCourseId(course.id.toLong())
                    .collectList()
                    .map { reviews -> course to reviews }
            }
            .map { (course, reviews) ->
                val courseDto = courseMapper.convertToDto(course)
                val reviewsDto = courseMapper.convertToDto(reviews)
                courseDto.copy(reviews = reviewsDto)
            }
            .collectList()

    private fun mergeDtoWithCourse(courseDto: CourseDto, course: Course) = Course(
        id = courseDto.id ?: course.id,
        title = courseDto.title ?: course.title,
        description = courseDto.description ?: course.description,
        price = courseDto.price ?: course.price,
        duration = courseDto.duration ?: course.duration,
        category = courseDto.category ?: course.category,
        tags = courseDto.tags ?: course.tags,
        level = courseDto.level ?: course.level,
        instructorId = courseDto.instructorId ?: course.instructorId,
        lessonIds = courseDto.lessonIds ?: course.lessonIds,
        preRequisites = courseDto.preRequisites ?: course.preRequisites,
        imageUrl = courseDto.imageUrl ?: course.imageUrl,
        published = courseDto.published ?: course.published,
        createdAt = course.createdAt,
        updatedAt = LocalDateTime.now(),
    )
}
