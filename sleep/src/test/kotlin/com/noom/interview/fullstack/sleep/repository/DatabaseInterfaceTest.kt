package com.noom.interview.fullstack.sleep.repository

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet


@ExtendWith(MockitoExtension::class)
class DatabaseInterfaceTest {

    @Mock
    private lateinit var connection: Connection

    @Mock
    private lateinit var preparedStatement: PreparedStatement

    @InjectMocks
    private lateinit var db: DatabaseInterface

    @BeforeEach
    fun setUp() {
        `when`(connection.prepareStatement(anyString()))
            .thenReturn(preparedStatement)
    }


    @Test
    fun testFindAll() {
        val resultSet = mock(ResultSet::class.java)
        `when`(preparedStatement.executeQuery()).thenReturn(resultSet)
        `when`(resultSet.next()).thenReturn(true).thenReturn(false)
        `when`(resultSet.getInt("id")).thenReturn(4)
        `when`(resultSet.getString("str")).thenReturn("value")

        val results = db.findAll(::deserializeTestClass, "sql query")

        Assertions.assertIterableEquals(listOf(TestClass(4, "value")), results)
    }

    @Test
    fun testFindOneWithOneResult() {
        val resultSet = mock(ResultSet::class.java)
        `when`(preparedStatement.executeQuery()).thenReturn(resultSet)
        `when`(resultSet.next()).thenReturn(true).thenReturn(false)
        `when`(resultSet.getInt("id")).thenReturn(4)
        `when`(resultSet.getString("str")).thenReturn("value")

        val result = db.findOne(::deserializeTestClass, "sql query")

        Assertions.assertEquals(TestClass(4, "value"), result)
    }

    @Test
    fun testFindOneWithNoResults() {
        val resultSet = mock(ResultSet::class.java)
        `when`(preparedStatement.executeQuery()).thenReturn(resultSet)
        `when`(resultSet.next()).thenReturn(false)

        val result = db.findOne(::deserializeTestClass, "sql query")

        Assertions.assertNull(result)
    }

    @Test
    fun testFindOneWithTooManyResultsThrowsException() {
        val resultSet = mock(ResultSet::class.java)
        `when`(preparedStatement.executeQuery()).thenReturn(resultSet)
        `when`(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false)
        `when`(resultSet.getInt("id")).thenReturn(4)
        `when`(resultSet.getString("str")).thenReturn("value")

        Assertions.assertThrows(IllegalStateException::class.java, { db.findOne(::deserializeTestClass, "sql query") })
    }
}

data class TestClass(private var id: Int, private var str: String)

fun deserializeTestClass(resultSet: ResultSet): TestClass {
    return TestClass(
        id = resultSet.getInt("id"),
        str = resultSet.getString("str"),
    )
}