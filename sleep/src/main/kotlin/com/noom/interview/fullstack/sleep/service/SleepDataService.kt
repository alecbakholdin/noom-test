package com.noom.interview.fullstack.sleep.service

import com.noom.interview.fullstack.sleep.exception.NotFoundException
import com.noom.interview.fullstack.sleep.model.SleepData
import com.noom.interview.fullstack.sleep.model.SleepDataPayload
import com.noom.interview.fullstack.sleep.model.User
import com.noom.interview.fullstack.sleep.repository.SleepDataRepository
import org.springframework.stereotype.Service
import java.sql.Date
import java.sql.Time

@Service
class SleepDataService(private val sleepDataRepository: SleepDataRepository) {
    fun createSleepData(sleepData: SleepDataPayload, user: User) {
        val diffTime = sleepData.timeEnd.minusSeconds(sleepData.timeStart.toSecondOfDay().toLong())
        val seconds = diffTime.toSecondOfDay().toFloat()
        val hours = seconds / 3600f
        sleepDataRepository.createSleepData(
            SleepData(
                id = 0,
                userId = user.id,
                date = Date.valueOf(sleepData.date),
                timeStart = Time.valueOf(sleepData.timeStart),
                timeEnd = Time.valueOf(sleepData.timeEnd),
                durationHours = hours,
                quality = sleepData.quality
            )
        )
    }

    fun getLastSleep(user: User): SleepData {
        return sleepDataRepository.getLastSleepData(user.id)
            ?: throw NotFoundException("User does not have any logged sleep data")
    }
}