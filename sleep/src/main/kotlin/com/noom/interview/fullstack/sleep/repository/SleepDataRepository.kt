package com.noom.interview.fullstack.sleep.repository

import com.noom.interview.fullstack.sleep.model.SleepData
import org.springframework.stereotype.Repository

@Repository
class SleepDataRepository(private val db: DatabaseInterface) {
    fun createSleepData(data: SleepData) {
        val query = """INSERT INTO sleep_data 
            |(user_id, date, time_start, duration_hours, quality) VALUES
            |(?, ?, ?, ?, ?)
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
}