package com.demo.learnify.services

import com.demo.learnify.data.models.Enrollment
import com.demo.learnify.data.repositories.EnrollmentRepository
import com.demo.learnify.dtos.CourseEnrollmentDto
import com.demo.learnify.dtos.EnrollmentDto
import com.demo.learnify.errorhandler.exceptions.NotFoundException
import com.demo.learnify.mappers.CourseMapper
import com.demo.learnify.mappers.EnrollmentMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class EnrollmentServiceImpl(
    private val enrollmentRepository: EnrollmentRepository,
    private val enrollmentMapper: EnrollmentMapper,
    private val courseMapper: CourseMapper
) : EnrollmentService {

    @Transactional
    override fun createEnrollment(request: EnrollmentDto): Mono<EnrollmentDto> =
        enrollmentMapper.convertToEnrollment(request).toMono()
            .flatMap { enrollmentRepository.save(it) }
            .map { enrollmentMapper.convertToDto(it) }

    @Transactional
    override fun updateEnrollment(request: EnrollmentDto): Mono<EnrollmentDto> =
        enrollmentRepository.findById(request.id.toLong())
            .flatMap { existingEnrollment ->
                val updatedEnrollment = mergeDtoWithEnrollment(request, existingEnrollment)
                enrollmentRepository.save(updatedEnrollment)
            }
            .map { enrollmentMapper.convertToDto(it) }

    override fun getAllEnrollmentsByUser(studentId: Long): Mono<List<CourseEnrollmentDto>> =
        enrollmentRepository.findAllEnrolledCoursesByStudentId(studentId)
            .switchIfEmpty(Mono.error(NotFoundException("There are no enrollments for this user")))
            .map { enrolledCourses ->
                val courseDto = courseMapper.convertToDto(enrolledCourses.course)
                val enrollmentDto = enrollmentMapper.convertToDto(enrolledCourses.enrollment)
                CourseEnrollmentDto(courseDto, enrollmentDto)
            }
            .collectList()

    override fun deleteEnrollment(id: Long): Mono<Void> =
        enrollmentRepository.deleteById(id)

    private fun mergeDtoWithEnrollment(enrollmentDto: EnrollmentDto, enrollment: Enrollment) =
        enrollment.copy(
            certificateUrl = enrollmentDto.certificateUrl ?: enrollment.certificateUrl,
            progress = enrollmentDto.progress ?: enrollment.progress,
            completed = enrollmentDto.completed ?: enrollment.completed
        )
}
