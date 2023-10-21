package com.demo.learnify.data.repositories

import com.demo.learnify.data.mappers.UserDataMapper
import com.demo.learnify.data.models.User
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.r2dbc.core.DatabaseClient
import reactor.core.publisher.Mono
import java.time.LocalDateTime

interface UserRepository : ReactiveCrudRepository<User, Long>, CustomUserRepository {
    fun findByEmail(email: String): Mono<User>
}

interface CustomUserRepository {
    fun saveUser(user: User): Mono<User>
}

class CustomUserRepositoryImpl(
    private val databaseClient: DatabaseClient,
    private val mapper: UserDataMapper
) : CustomUserRepository {
    override fun saveUser(user: User): Mono<User> =
        databaseClient.sql(INSERT_USER)
            .bind("first_name", user.firstName)
            .bind("last_name", user.lastName)
            .bind("profile_picture", user.profilePictureUrl ?: "")
            .bind("email", user.email)
            .bind("password", user.password)
            .bind("roles", user.roles.toTypedArray())
            .bind("bio", user.bio ?: "")
            .filter { statement, _ -> statement.returnGeneratedValues("id", "created_at", "updated_at").execute() }
            .fetch()
            .first()
            .map {
                user.copy(
                    id = it["id"] as Int,
                    createdAt = it["created_at"] as LocalDateTime,
                    updatedAt = it["updated_at"] as LocalDateTime
                )
            }

    companion object {
        const val INSERT_USER = """
            INSERT INTO users (first_name, last_name, profile_picture, email, password, roles, bio)
            VALUES (:first_name, :last_name, :profile_picture, :email, :password, :roles, :bio)
        """
    }
}
