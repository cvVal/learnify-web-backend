package com.demo.learnify.data.mappers

import com.demo.learnify.data.models.User
import io.r2dbc.spi.Row
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDateTime
import java.util.function.BiFunction

@Component
class UserDataMapper : BiFunction<Row, Any, User> {
    override fun apply(row: Row, o: Any): User {
        val id =                row.get("id", Number::class.java)!!.toInt()
        val firstName =         row.get("first_name", String::class.java) ?: ""
        val lastName =          row.get("last_name", String::class.java) ?: ""
        val profilePictureUrl = row.get("profile_picture", String::class.java)
        val email =             row.get("email", String::class.java) ?: ""
        val password =          row.get("password", String::class.java) ?: ""
        val rolesArray =        row.get("roles", Array::class.java)
        val roles = rolesArray?.map { it.toString() }?.toSet() ?: emptySet()
        val bio =               row.get("bio", String::class.java)
        val createdAt =         row.get("created_at", LocalDateTime::class.java)
        val updatedAt =         row.get("updated_at", LocalDateTime::class.java)
        return User(
            id = id,
            firstName = firstName,
            lastName = lastName,
            profilePictureUrl = profilePictureUrl,
            email = email,
            password = password,
            roles = roles,
            bio = bio,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
