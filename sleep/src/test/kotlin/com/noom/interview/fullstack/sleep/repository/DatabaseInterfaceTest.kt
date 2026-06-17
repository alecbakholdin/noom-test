package com.noom.interview.fullstack.sleep.repository

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
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
        whenever(connection.prepareStatement(anyString())).thenReturn(preparedStatement)
    }


    @Test
    fun testFindAll() {
        val resultSet = mock(ResultSet::class.java)
        whenever(preparedStatement.executeQuery()).thenReturn(resultSet)
        whenever(resultSet.next()).thenReturn(true).thenReturn(false)
        whenever(resultSet.getInt("id")).thenReturn(4)
        whenever(resultSet.getString("str")).thenReturn("value")

        val results = db.findAll(::deserializeTestClass, "SELECT 1", "paramOne", 2)

        Assertions.assertIterableEquals(listOf(TestClass(4, "value")), results)
        verify(preparedStatement).setString(1, "paramOne")
        verify(preparedStatement).setInt(2, 2)
    }

    @Test
    fun testPrepareStatementFailsOnUnknownType() {
        Assertions.assertThrows(
            IllegalArgumentException::class.java,
            { db.prepareStatement("SELECT 1", TestClass(1, "test")) }
        )
    }

    @Test
    fun testFindOneWithOneResult() {
        val resultSet = mock(ResultSet::class.java)
        whenever(preparedStatement.executeQuery()).thenReturn(resultSet)
        whenever(resultSet.next()).thenReturn(true).thenReturn(false)
        whenever(resultSet.getInt("id")).thenReturn(4)
        whenever(resultSet.getString("str")).thenReturn("value")

        val result = db.findOne(::deserializeTestClass, "SELECT 1")

        Assertions.assertEquals(TestClass(4, "value"), result)
    }

    @Test
    fun testFindOneWithNoResults() {
        val resultSet = mock(ResultSet::class.java)
        whenever(preparedStatement.executeQuery()).thenReturn(resultSet)
        whenever(resultSet.next()).thenReturn(false)

        val result = db.findOne(::deserializeTestClass, "SELECT 1")

        Assertions.assertNull(result)
    }

    @Test
    fun testFindOneWithTooManyResultsThrowsException() {
        val resultSet = mock(ResultSet::class.java)
        whenever(preparedStatement.executeQuery()).thenReturn(resultSet)
        whenever(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false)
        whenever(resultSet.getInt("id")).thenReturn(4)
        whenever(resultSet.getString("str")).thenReturn("value")

        Assertions.assertThrows(IllegalStateException::class.java, {
            db.findOne(::deserializeTestClass, "SELECT 1")
        })
    }
}

data class TestClass(private var id: Int, private var str: String)

fun deserializeTestClass(resultSet: ResultSet): TestClass {
    return TestClass(
        id = resultSet.getInt("id"),
        str = resultSet.getString("str"),
    )
}