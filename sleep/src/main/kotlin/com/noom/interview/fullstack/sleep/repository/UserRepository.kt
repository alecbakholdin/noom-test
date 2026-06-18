package com.noom.interview.fullstack.sleep.repository

import com.noom.interview.fullstack.sleep.model.User
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class UserRepository(
    private val db: DatabaseInterface
) {
    fun getUserByUsername(username: String): User? {
        val query = "SELECT id, username FROM users WHERE username = ?"
        return db.findOne(::deserializeUser, query, username)
    }

    fun getUserById(id: Int): User {
        val query = "SELECT id, username FROM users WHERE id = ?"
        val user = db.findOne(::deserializeUser, query, id)
            ?: throw IllegalStateException("User with id $id not found")
        return user
    }

    fun createUser(user: User) {
        val query = "INSERT INTO users (username) VALUES (?)"
        val updateCount = db.prepareStatement(query, user.username).executeUpdate()
        if (updateCount == 0) {
            throw IllegalStateException("Failed to create user")
        }
    }


    fun deserializeUser(resultSet: ResultSet): User {
        return User(
            id = resultSet.getInt("id"),
            username = resultSet.getString("username"),
        )
    }
}
