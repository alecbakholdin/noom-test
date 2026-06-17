package com.noom.interview.fullstack.sleep.repository

import com.noom.interview.fullstack.sleep.model.User
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import java.sql.PreparedStatement
import java.sql.ResultSet

@ExtendWith(MockitoExtension::class)
class UserRepositoryTest {
    @Mock
    private lateinit var db: DatabaseInterface

    @InjectMocks
    private lateinit var userRepository: UserRepository

    @Test
    fun testDeserializer() {
        val resultSet = mock(ResultSet::class.java)
        whenever(resultSet.getString("username")).thenReturn("user")
        whenever(resultSet.getInt("id")).thenReturn(4)
        val user = userRepository.deserializeUser(resultSet)
        Assertions.assertEquals(User(4, "user"), user)
    }


    @Test
    fun testGetUserByUsername() {
        whenever(db.findOne<User>(anyOrNull(), anyString(), eq("user"))).thenReturn(
            User(
                id = 4,
                username = "user"
            )
        )

        val user = userRepository.getUserByUsername("user")
        Assertions.assertEquals(User(4, "user"), user)
    }

    @Test
    fun testGetUserById() {
        whenever(db.findOne<User>(anyOrNull(), anyString(), eq(4))).thenReturn(
            User(
                id = 4,
                username = "user"
            )
        )

        val user = userRepository.getUserById(4)
        Assertions.assertEquals(User(4, "user"), user)
    }

    @Test
    fun testCreateUser() {
        val stmt: PreparedStatement = mock()
        whenever(db.prepareStatement(anyString(), eq("user"))).thenReturn(stmt)
        whenever(stmt.executeUpdate()).thenReturn(1)

        userRepository.createUser(User(4, "user"))

        verify(stmt).executeUpdate()
    }

    @Test
    fun testCreateUserDoesntCreateThrowsException() {
        val stmt: PreparedStatement = mock()
        whenever(db.prepareStatement(anyString(), eq("user"))).thenReturn(stmt)
        whenever(stmt.executeUpdate()).thenReturn(0)

        Assertions.assertThrows(
            IllegalStateException::class.java,
            { userRepository.createUser(User(4, "user")) }
        )
    }
}