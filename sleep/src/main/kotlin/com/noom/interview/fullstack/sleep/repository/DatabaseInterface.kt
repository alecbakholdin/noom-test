package com.noom.interview.fullstack.sleep.repository

import org.intellij.lang.annotations.Language
import org.springframework.stereotype.Service
import java.sql.*


@Service
class DatabaseInterface(
    private var connection: Connection
) {
    fun <T> findAll(deserializer: (ResultSet) -> T, @Language("SQL") query: String, vararg params: Any): List<T> {
        val items = mutableListOf<T>()
        prepareStatement(query, *params).use { stmt ->
            stmt.executeQuery().use { resultSet ->
                while (resultSet.next()) {
                    items.add(deserializer(resultSet))
                }
            }
        }
        return items
    }

    fun <T> findOne(deserializer: (ResultSet) -> T, @Language("SQL") query: String, vararg params: Any): T? {
        val items = findAll(deserializer, query, *params)
        if (items.size == 1) return items[0]
        if (items.isEmpty()) return null
        throw IllegalStateException("Query returned more than one entry")
    }

    fun prepareStatement(@Language("SQL") query: String, vararg params: Any): PreparedStatement {
        val stmt = connection.prepareStatement(query)
        params.forEachIndexed { index, param ->
            when (param) {
                is String -> stmt.setString(index + 1, param)
                is Int -> stmt.setInt(index + 1, param)
                is Date -> stmt.setDate(index + 1, param)
                is Time -> stmt.setTime(index + 1, param)
                else -> throw IllegalArgumentException("Unsupported param type " + param::class.qualifiedName)
            }
        }
        return stmt
    }
}