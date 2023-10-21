package com.demo.learnify.mappers

import com.demo.learnify.data.models.Lesson
import com.demo.learnify.data.models.Question
import com.demo.learnify.data.models.Quiz
import com.demo.learnify.data.models.Resource
import com.demo.learnify.dtos.LessonDto
import com.demo.learnify.dtos.QuestionDto
import com.demo.learnify.dtos.QuizDto
import com.demo.learnify.dtos.ResourceDto
import org.mapstruct.*

@Mapper
interface LessonMapper {

    @Mapping(target = "status", source = "status", qualifiedByName = ["toStatusString"])
    fun convertToLesson(lessonDto: LessonDto): Lesson

    @Mapping(target = "status", source = "status", qualifiedByName = ["toStatus"])
    fun convertToLessonDto(lesson: Lesson): LessonDto

    @Named("toStatusString")
    fun toStatusString(status: LessonDto.LessonStatus): String = status.name

    @Named("toStatus")
    @ValueMapping(source = MappingConstants.ANY_UNMAPPED, target = "NOT_STARTED")
    fun toStatus(status: String): LessonDto.LessonStatus =
        when(status.uppercase()) {
            "NOT_STARTED" -> LessonDto.LessonStatus.NOT_STARTED
            "IN_PROGRESS" -> LessonDto.LessonStatus.IN_PROGRESS
            "COMPLETED" -> LessonDto.LessonStatus.COMPLETED
            else -> throw IllegalArgumentException("Invalid status: $status")
        }

    fun convertToQuiz(quizDto: QuizDto): Quiz
    fun convertToQuizDto(quiz: Quiz): QuizDto
    fun convertToQuestion(questionDto: QuestionDto): Question
    fun convertToQuestionDto(question: Question): QuestionDto

    fun convertToResource(resourceDto: ResourceDto): Resource
    fun convertToResourceDto(resource: Resource): ResourceDto
}
