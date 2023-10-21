package com.demo.learnify.data.repositories

import com.demo.learnify.data.mappers.CourseDataMapper
import com.demo.learnify.data.models.Course
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.r2dbc.core.DatabaseClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant
import java.time.LocalDateTime

interface CourseRepository : ReactiveCrudRepository<Course, Long>, CustomCourseRepository

interface CustomCourseRepository {
    fun saveCourse(course: Course): Mono<Course>
    fun updateCourse(course: Course): Mono<Course>
    fun findAllCourses(): Flux<Course>
    fun findTopCoursesByRating(limit: Int = 5): Flux<Course>
    fun findAllByTitleContaining(title: String): Flux<Course>
    fun findAllByInstructorId(instructorId: Int): Flux<Course>
    fun findAllByCategory(category: String): Flux<Course>
    fun findAllByLevel(level: String): Flux<Course>
}

class CustomCourseRepositoryImpl(
    private val databaseClient: DatabaseClient,
    private val mapper: CourseDataMapper
) : CustomCourseRepository {
    override fun saveCourse(course: Course): Mono<Course> =
        databaseClient.sql(INSERT_COURSE)
            .bind("title", course.title)
            .bind("description", course.description)
            .bind("price", course.price ?: 0.0)
            .bind("duration", course.duration ?: 0)
            .bind("category", course.category ?: "")
            .bind("tags", course.tags?.toTypedArray() ?: emptyArray<String>())
            .bind("level", course.level ?: "")
            .bind("instructor_id", course.instructorId)
            .bind("lesson_ids", course.lessonIds?.toTypedArray() ?: emptyArray<String>())
            .bind("pre_requisites", course.preRequisites ?: "")
            .bind("image_url", course.imageUrl ?: "")
            .bind("is_published", course.published)
            .filter { statement, _ -> statement.returnGeneratedValues("id", "created_at", "updated_at").execute() }
            .fetch()
            .first()
            .map {
                course.copy(
                    id = it["id"] as Int,
                    createdAt = it["created_at"] as LocalDateTime,
                    updatedAt = it["updated_at"] as LocalDateTime
                )
            }

    override fun updateCourse(course: Course): Mono<Course> =
        databaseClient.sql(UPDATE_COURSE)
            .bind("id", course.id)
            .bind("title", course.title)
            .bind("description", course.description)
            .bind("price", course.price ?: 0.0)
            .bind("duration", course.duration ?: 0)
            .bind("category", course.category ?: "")
            .bind("tags", course.tags?.toTypedArray() ?: emptyArray<String>())
            .bind("level", course.level ?: "")
            .bind("instructor_id", course.instructorId)
            .bind("lesson_ids", course.lessonIds?.toTypedArray() ?: emptyArray<String>())
            .bind("pre_requisites", course.preRequisites ?: "")
            .bind("image_url", course.imageUrl ?: "")
            .bind("is_published", course.published)
            .bind("updated_at", Instant.now())
            .fetch()
            .rowsUpdated()
            .flatMap { if (it > 0) Mono.just(course) else Mono.empty() }

    override fun findAllCourses(): Flux<Course> =
        databaseClient.sql(FIND_ALL)
            .map(mapper::apply)
            .all()

    override fun findTopCoursesByRating(limit: Int): Flux<Course> =
        databaseClient.sql(FIND_TOP_COURSES_BY_RATING_JOIN_REVIEWS)
            .bind("limit", limit)
            .map(mapper::apply)
            .all()

    override fun findAllByTitleContaining(title: String): Flux<Course> =
        databaseClient.sql(FIND_ALL_BY_TITLE_CONTAINING)
            .bind("title", title)
            .map(mapper::apply)
            .all()

    override fun findAllByInstructorId(instructorId: Int): Flux<Course> =
        databaseClient.sql(FIND_ALL_BY_INSTRUCTOR_ID)
            .bind("instructor_id", instructorId)
            .map(mapper::apply)
            .all()

    override fun findAllByCategory(category: String): Flux<Course> =
        databaseClient.sql(FIND_ALL_BY_CATEGORY)
            .bind("category", category)
            .map(mapper::apply)
            .all()

    override fun findAllByLevel(level: String): Flux<Course> =
        databaseClient.sql(FIND_ALL_BY_LEVEL)
            .bind("level", level)
            .map(mapper::apply)
            .all()

    companion object {
        const val INSERT_COURSE = """
            INSERT INTO courses (
                title,
                description,
                price,
                duration,
                category,
                tags,
                level,
                instructor_id,
                lesson_ids,
                pre_requisites,
                image_url,
                is_published
            )
            VALUES (
                :title,
                :description,
                :price,
                :duration,
                :category,
                :tags,
                :level,
                :instructor_id,
                :lesson_ids,
                :pre_requisites,
                :image_url,
                :is_published
            )
        """

        const val UPDATE_COURSE = """
            UPDATE courses SET
                title = :title,
                description = :description,
                price = :price,
                duration = :duration,
                category = :category,
                tags = :tags,
                level = :level,
                instructor_id = :instructor_id,
                lesson_ids = :lesson_ids,
                pre_requisites = :pre_requisites,
                image_url = :image_url,
                is_published = :is_published,
                updated_at = :updated_at
            WHERE id = :id
        """

        const val FIND_ALL = """
            SELECT c.id, c.title, c.description, c.price, c.duration,
                    c.category, c.tags, c.level, c.instructor_id,
                    c.lesson_ids, c.pre_requisites, c.image_url, 
                    c.is_published, c.created_at, c.updated_at
            FROM courses c
            WHERE c.is_published = 'true'
        """

        const val FIND_TOP_COURSES_BY_RATING_JOIN_REVIEWS = """
            SELECT c.id, c.title, c.description, c.price, c.duration,
                    c.category, c.tags, c.level, c.instructor_id,
                    c.lesson_ids, c.pre_requisites, c.image_url, 
                    c.is_published, c.created_at, c.updated_at, 
                    AVG(r.rating) as rating
            FROM courses c
            INNER JOIN reviews r ON c.id = r.course_id
            WHERE c.is_published = 'true'
            GROUP BY c.id, c.title, c.description, c.price, c.duration,
                    c.category, c.tags, c.level, c.instructor_id,
                    c.lesson_ids, c.pre_requisites, c.image_url, 
                    c.is_published, c.created_at, c.updated_at
            ORDER BY AVG(r.rating) DESC
            LIMIT :limit
        """

        //"SELECT * FROM courses WHERE title LIKE '%' || :title || '%'"
        const val FIND_ALL_BY_TITLE_CONTAINING = """
            SELECT c.id, c.title, c.description, c.price, c.duration,
                    c.category, c.tags, c.level, c.instructor_id,
                    c.lesson_ids, c.pre_requisites, c.image_url, 
                    c.is_published, c.created_at, c.updated_at
            FROM courses c 
            WHERE title LIKE CONCAT('%', :title, '%')
            AND c.is_published = 'true'
        """

        const val FIND_ALL_BY_INSTRUCTOR_ID = """
            SELECT c.id, c.title, c.description, c.price, c.duration,
                    c.category, c.tags, c.level, c.instructor_id,
                    c.lesson_ids, c.pre_requisites, c.image_url, 
                    c.is_published, c.created_at, c.updated_at 
            FROM courses c
            WHERE instructor_id = :instructor_id
            AND c.is_published = 'true'
        """

        const val FIND_ALL_BY_CATEGORY = """
            SELECT c.id, c.title, c.description, c.price, c.duration,
                    c.category, c.tags, c.level, c.instructor_id,
                    c.lesson_ids, c.pre_requisites, c.image_url, 
                    c.is_published, c.created_at, c.updated_at 
            FROM courses c
            WHERE category = :category
            AND c.is_published = 'true'
        """

        const val FIND_ALL_BY_LEVEL = """
            SELECT c.id, c.title, c.description, c.price, c.duration,
                    c.category, c.tags, c.level, c.instructor_id,
                    c.lesson_ids, c.pre_requisites, c.image_url, 
                    c.is_published, c.created_at, c.updated_at
            FROM courses c
            WHERE level = :level
            AND c.is_published = 'true'
        """
    }
}
