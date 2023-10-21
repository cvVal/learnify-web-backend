package com.demo.learnify.services

import com.demo.learnify.data.models.Question
import com.demo.learnify.data.repositories.QuestionRepository
import com.demo.learnify.data.repositories.QuizRepository
import com.demo.learnify.dtos.QuestionDto
import com.demo.learnify.dtos.QuizDto
import com.demo.learnify.errorhandler.exceptions.NotFoundException
import com.demo.learnify.mappers.LessonMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class QuizServiceImpl(
    private val quizRepository: QuizRepository,
    private val questionRepository: QuestionRepository,
    private val lessonMapper: LessonMapper
) : QuizService {

    @Transactional
    override fun createQuiz(quizDto: QuizDto): Mono<QuizDto> =
        lessonMapper.convertToQuiz(quizDto).toMono()
            .flatMap { quizRepository.saveQuiz(it) }
            .flatMap { quiz ->
                val questions = quizDto.questions?.map {
                    Question(
                        id = 0,
                        quizId = quiz.id,
                        question = it.question!!,
                        options = it.options!!,
                        correctOption = it.correctOption!!
                    )
                }
                questionRepository.saveQuestions(questions!!)
                    .collectList()
                    .map { quiz.copy(questions = it) }
            }
            .map { lessonMapper.convertToQuizDto(it) }

    @Transactional
    override fun updateQuiz(questionDto: QuestionDto): Mono<QuestionDto> =
        questionRepository.findById(questionDto.id!!.toLong())
            .flatMap { currentQuestion ->
                val updatedQuestion = mergeDtoWithQuestion(currentQuestion, questionDto)
                questionRepository.updateQuestion(updatedQuestion)
            }
            .map { lessonMapper.convertToQuestionDto(it) }

    override fun getQuiz(id: Long): Mono<QuizDto> =
        quizRepository.findById(id)
            .switchIfEmpty(Mono.error(NotFoundException("Quiz not found")))
            .flatMap { quiz ->
                questionRepository.findAllByQuizId(quiz.id.toLong())
                    .collectList()
                    .map { quiz.copy(questions = it) }
            }
            .map { lessonMapper.convertToQuizDto(it) }

    @Transactional
    override fun deleteQuiz(id: Long): Mono<Void> =
        questionRepository.deleteByQuizId(id)
            .then(quizRepository.deleteById(id))

    private fun mergeDtoWithQuestion(question: Question, questionDto: QuestionDto) =
        question.copy(
            question = questionDto.question ?: question.question,
            options = questionDto.options ?: question.options,
            correctOption = questionDto.correctOption ?: question.correctOption
        )
}
