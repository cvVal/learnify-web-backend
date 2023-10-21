package com.demo.learnify.mappers

import com.demo.learnify.data.models.Review
import com.demo.learnify.dtos.ReviewDto
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper
interface ReviewMapper {

    @Mapping(target = "email", ignore = true)
    fun convertToReview(review: ReviewDto): Review
    fun convertToDto(review: Review): ReviewDto
}
