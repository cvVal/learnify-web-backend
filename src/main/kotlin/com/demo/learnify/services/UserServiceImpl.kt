package com.demo.learnify.services

import com.demo.learnify.data.repositories.UserRepository
import com.demo.learnify.dtos.CreateUserDto
import com.demo.learnify.dtos.UpdateUserDto
import com.demo.learnify.errorhandler.exceptions.ForbiddenException
import com.demo.learnify.errorhandler.exceptions.NotFoundException
import com.demo.learnify.mappers.UserMapper
import com.demo.learnify.transformers.UserTransformer
import org.springframework.dao.DuplicateKeyException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val userMapper: UserMapper,
    private val userTransformer: UserTransformer
) : UserService {

    @Transactional
    override fun createUser(userRequest: CreateUserDto): Mono<CreateUserDto> =
        userRequest.toMono()
            .map { userMapper.convertToUser(it.copy(password = passwordEncoder.encode(it.password))) }
            .flatMap { userRepository.saveUser(it) }
            .map { userMapper.convertToDto(it) }
            .onErrorResume(DuplicateKeyException::class.java) { error ->
                Mono.error(error)
            }
//            .onErrorMap { e ->
//                NotFoundException(e.message!!, e)
//            }

    @Transactional
    override fun updateUser(userRequest: UpdateUserDto, name: String): Mono<CreateUserDto> =
        userRepository.findById(userRequest.id.toLong())
            .switchIfEmpty(Mono.error(NotFoundException("User not found")))
            .filter { it.email == name }
            .switchIfEmpty(Mono.error(ForbiddenException("This user cannot update the review")))
            .flatMap { existingUser ->
                val updatedUser = userTransformer.mergeUserDtoWithUser(userRequest, existingUser)
                userRepository.save(updatedUser)
            }
            .map { userTransformer.convertToDto(it) }

    override fun getAllUsers(): Mono<List<CreateUserDto>> =
        userRepository.findAll()
            .switchIfEmpty(Mono.error(NotFoundException("There are no users")))
            .map { user ->
                userMapper.convertToDto(user)
            }
            .collectList()

    override fun getUser(id: Long): Mono<CreateUserDto> =
        userRepository.findById(id)
            .switchIfEmpty(Mono.error(NotFoundException("User not found")))
            .map { user ->
                userTransformer.convertToDto(user)
            }

    override fun getUserByEmail(email: String): Mono<CreateUserDto> =
        userRepository.findByEmail(email)
            .switchIfEmpty(Mono.error(NotFoundException("User not found")))
            .map { userMapper.convertToDto(it) }

    override fun deleteUser(id: Long): Mono<Void> =
        userRepository.deleteById(id)
}
