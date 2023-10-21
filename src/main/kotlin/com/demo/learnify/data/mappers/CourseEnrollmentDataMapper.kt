package com.demo.learnify.data.mappers

import com.demo.learnify.data.models.CourseEnrollment
import io.r2dbc.spi.Row
import org.springframework.stereotype.Component
import java.util.function.BiFunction

@Component
class CourseEnrollmentDataMapper(
    private val courseDataMapper: CourseDataMapper,
    private val enrollmentDataMapper: EnrollmentDataMapper
) : BiFunction<Row, Any, CourseEnrollment> {
    override fun apply(row: Row, o: Any): CourseEnrollment {
        val course = courseDataMapper.apply(row, o)
        val enrollment = enrollmentDataMapper.apply(row, o)
        return CourseEnrollment(
            course = course,
            enrollment = enrollment
        )
    }
}
