package com.demo.learnify.data.repositories

import com.demo.learnify.data.mappers.CourseEnrollmentDataMapper
import com.demo.learnify.data.models.CourseEnrollment
import com.demo.learnify.data.models.Enrollment
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.r2dbc.core.DatabaseClient
import reactor.core.publisher.Flux

interface EnrollmentRepository : ReactiveCrudRepository<Enrollment, Long>, CustomEnrollmentRepository

interface CustomEnrollmentRepository {
    fun findAllEnrolledCoursesByStudentId(studentId: Long): Flux<CourseEnrollment>
}

class CustomEnrollmentRepositoryImpl(
    private val databaseClient: DatabaseClient,
    private val mapper: CourseEnrollmentDataMapper
) : CustomEnrollmentRepository {
    override fun findAllEnrolledCoursesByStudentId(studentId: Long): Flux<CourseEnrollment> =
        databaseClient.sql(SELECT_ALL_COURSES)
            .bind("studentId", studentId)
            .map(mapper::apply)
            .all()

    companion object {
        const val SELECT_ALL_COURSES = """
            SELECT c.id, c.title, c.description, c.price, c.duration, c.category, c.tags, c.level, 
                c.instructor_id, c.lesson_ids, c.pre_requisites, c.image_url, c.is_published, 
                c.created_at, c.updated_at, 
                e.id as enrollment_id, e.course_id, e.student_id, e.enrollment_date, 
                e.certificate_url, e.progress_percent, e.is_completed
            FROM courses c
            JOIN enrollments e ON c.id = e.course_id
            WHERE e.student_id = :studentId;
        """
    }
}
