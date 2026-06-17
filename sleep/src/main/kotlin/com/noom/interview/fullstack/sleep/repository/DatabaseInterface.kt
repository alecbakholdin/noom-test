package com.noom.interview.fullstack.sleep.repository

import org.springframework.stereotype.Service
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

sealed interface Param {
    data class Text(val value: String) : Param
    data class Integer(val value: Int) : Param
}

@Service
class DatabaseInterface(
    private var connection: Connection
) {
    fun <T> findAll(deserializer: (ResultSet) -> T, query: String, vararg params: Param): List<T> {
        val items = mutableListOf<T>()
        prepareStatement(query, *params).use { stmt ->
            stmt.executeQuery().use { resultSet ->
                while (resultSet.next()) {
                    items.add(deserializer(resultSet))
                }
            }
        }
        return items;
    }

    fun <T> findOne(deserializer: (ResultSet) -> T, query: String, vararg params: Param): T? {
        val items = findAll(deserializer, query, *params)
        if (items.size == 1) return items[0]
        if (items.isEmpty()) return null
        throw IllegalStateException("Query returned more than one entry")
    }

    fun prepareStatement(query: String, vararg params: Param): PreparedStatement {
        val stmt = connection.prepareStatement(query)
        params.forEachIndexed { index, param ->
            when (param) {
                is Param.Text -> stmt.setString(index, param.value)
                is Param.Integer -> stmt.setInt(index, param.value)
            }
        }
        return stmt
    }
}