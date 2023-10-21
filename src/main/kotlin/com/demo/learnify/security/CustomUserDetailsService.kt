package com.demo.learnify.security

import com.demo.learnify.data.repositories.UserRepository
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : ReactiveUserDetailsService {
    override fun findByUsername(username: String): Mono<UserDetails> {
        return userRepository.findByEmail(username)
            .switchIfEmpty(Mono.error(UsernameNotFoundException("User not found: $username")))
            .map { user ->
                User.builder()
                    .username(user.email)
                    .password(user.password)
                    .roles(*user.roles.toTypedArray())
                    .build()
            }
    }
}
