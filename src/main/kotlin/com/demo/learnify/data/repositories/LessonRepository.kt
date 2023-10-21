package com.demo.learnify.data.repositories

import com.demo.learnify.data.mappers.LessonDataMapper
import com.demo.learnify.data.models.Lesson
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.r2dbc.core.DatabaseClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

interface LessonRepository : ReactiveCrudRepository<Lesson, Long>, CustomLessonRepository

interface CustomLessonRepository {
    fun saveLesson(lesson: Lesson): Mono<Lesson>
    fun updateLesson(lesson: Lesson): Mono<Lesson>
    fun findAllLessonsByCourseId(courseId: Long): Flux<Lesson>
}

class CustomLessonRepositoryImpl(
    private val databaseClient: DatabaseClient,
    private val mapper: LessonDataMapper
) : CustomLessonRepository {

    override fun saveLesson(lesson: Lesson): Mono<Lesson> =
        databaseClient.sql(INSERT_LESSON)
            .bind("title", lesson.title)
            .bind("description", lesson.description)
            .bind("duration", lesson.duration ?: 0)
            .bind("status", lesson.status)
            .bind("courseId", lesson.courseId)
            .bind("contentUrl", lesson.contentUrl ?: "")
            .bind("order", lesson.order)
            .filter { statement, _ -> statement.returnGeneratedValues("id", "created_at", "updated_at").execute() }
            .fetch()
            .first()
            .map {
                lesson.copy(
                    id = it["id"] as Int,
                    createdAt = it["created_at"] as LocalDateTime,
                    updatedAt = it["updated_at"] as LocalDateTime
                )
            }

    override fun updateLesson(lesson: Lesson): Mono<Lesson> =
        databaseClient.sql(UPDATE_LESSON)
            .bind("id", lesson.id)
            .bind("title", lesson.title)
            .bind("description", lesson.description)
            .bind("duration", lesson.duration ?: 0)
            .bind("status", lesson.status)
            .bind("contentUrl", lesson.contentUrl ?: "")
            .bind("order", lesson.order)
            .bind("updatedAt", LocalDateTime.now())
            .fetch()
            .rowsUpdated()
            .flatMap { if (it > 0) Mono.just(lesson) else Mono.empty() }

    override fun findAllLessonsByCourseId(courseId: Long): Flux<Lesson> =
        databaseClient.sql(SELECT_ALL_LESSONS_BY_COURSE_ID)
            .bind("courseId", courseId)
            .map(mapper::apply)
            .all()

    companion object {
        const val INSERT_LESSON = """
            INSERT INTO lessons (
                title,
                description,
                duration,
                status,
                course_id,
                content_url,
                order_number
            )
            VALUES (
                :title,
                :description,
                :duration,
                :status,
                :courseId,
                :contentUrl,
                :order
            )
        """
        const val UPDATE_LESSON = """
            UPDATE lessons 
            SET 
                title = :title,
                description = :description,
                duration = :duration,
                status = :status,
                content_url = :contentUrl,
                order_number = :order,
                updated_at = :updatedAt
            WHERE id = :id
        """
        const val SELECT_ALL_LESSONS_BY_COURSE_ID = """
            SELECT l.id, l.title, l.description, l.duration, l.status, l.course_id, l.content_url,
            l.order_number, l.created_at, l.updated_at
            FROM lessons l
            WHERE l.course_id = :courseId;
        """
    }

    /*
    const val SELECT_ALL_ENROLLMENTS_BY_USER_ID = """
    SELECT e.id, e.user_id, e.course_id, e.date_enrolled, c.title, c.description
    FROM enrollments e
    JOIN courses c ON e.course_id = c.id
    WHERE e.user_id = :userId;
"""

const val SELECT_ALL_COURSES_BY_ENROLLMENT_STATUS = """
    SELECT c.id, c.title, c.description
    FROM courses c
    WHERE c.id IN (
        SELECT e.course_id
        FROM enrollments e
        WHERE e.user_id = :userId AND e.status = :status
    );
"""

const val SELECT_ALL_LESSONS_BY_COURSE_ID = """
    SELECT l.id, l.course_id, l.title, l.description
    FROM lessons l
    WHERE l.course_id = :courseId;
"""

const val SELECT_ALL_QUESTIONS_BY_LESSON_ID = """
    SELECT q.id, q.lesson_id, q.question, q.correct_answer, q.incorrect_answer_1, q.incorrect_answer_2, q.incorrect_answer_3
    FROM questions q
    WHERE q.lesson_id = :lessonId;
"""

const val SELECT_ALL_RESOURCES_BY_COURSE_ID = """
    SELECT r.id, r.course_id, r.title, r.description, r.url
    FROM resources r
    WHERE r.course_id = :courseId;
"""

     */
}
