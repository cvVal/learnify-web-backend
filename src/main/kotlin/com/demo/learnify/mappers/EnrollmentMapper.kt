package com.demo.learnify.mappers

import com.demo.learnify.data.models.Enrollment
import com.demo.learnify.dtos.EnrollmentDto
import org.mapstruct.Mapper

@Mapper
interface EnrollmentMapper {

    fun convertToEnrollment(enrollmentDto: EnrollmentDto): Enrollment
    fun convertToDto(enrollment: Enrollment): EnrollmentDto
}
