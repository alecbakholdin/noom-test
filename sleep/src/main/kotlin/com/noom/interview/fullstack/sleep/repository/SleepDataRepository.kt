package com.noom.interview.fullstack.sleep.repository

import com.noom.interview.fullstack.sleep.model.SleepData
import com.noom.interview.fullstack.sleep.model.SleepQuality
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class SleepDataRepository(private val db: DatabaseInterface) {
    fun createSleepData(data: SleepData) {
        val query = """INSERT INTO sleep_data 
            |(user_id, date, time_start, duration_hours, quality) VALUES
            |(?, ?, ?, ?, ?::sleep_quality)
        """.trimMargin()
        val modifiedCount = db.prepareStatement(
            query,
            data.userId,
            data.date,
            data.timeStart,
            data.durationHours,
            data.quality
        ).executeUpdate()
        if (modifiedCount == 0) {
            throw IllegalStateException("Failed to create sleep data")
        }
    }

    fun getLastSleepData(userId: Int): SleepData? {
        val query = """SELECT 
            |  id,
            |  user_id,
            |  date,
            |  time_start,
            |  duration_hours,
            |  quality
            |FROM sleep_data 
            |WHERE user_id = ?
            |ORDER BY date DESC, time_start DESC
            |LIMIT 1
            |""".trimMargin()
        return db.findOne(::deserializeSleepData, query, userId)
    }

    fun deserializeSleepData(resultSet: ResultSet): SleepData {
        return SleepData(
            id = resultSet.getInt("id"),
            userId = resultSet.getInt("user_id"),
            date = resultSet.getDate("date"),
            timeStart = resultSet.getTime("time_start"),
            durationHours = resultSet.getInt("duration_hours"),
            quality = SleepQuality.valueOf(resultSet.getString("quality")),
        )
    }
}