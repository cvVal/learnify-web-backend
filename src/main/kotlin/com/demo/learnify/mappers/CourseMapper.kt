package com.demo.learnify.mappers

import com.demo.learnify.data.models.Course
import com.demo.learnify.data.models.Review
import com.demo.learnify.dtos.CourseDto
import com.demo.learnify.dtos.ReviewDto
import org.mapstruct.Mapper

@Mapper
interface CourseMapper {

    fun convertToCourse(courseDto: CourseDto): Course
    fun convertToDto(course: Course): CourseDto

    fun convertToReview(reviews: List<ReviewDto>): List<Review>
    fun convertToDto(reviews: List<Review>): List<ReviewDto>
}
