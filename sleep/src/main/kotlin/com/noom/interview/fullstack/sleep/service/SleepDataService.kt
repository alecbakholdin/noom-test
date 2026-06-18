package com.noom.interview.fullstack.sleep.service

import com.noom.interview.fullstack.sleep.exception.NotFoundException
import com.noom.interview.fullstack.sleep.model.*
import com.noom.interview.fullstack.sleep.repository.SleepDataRepository
import org.springframework.stereotype.Service
import java.sql.Date
import java.sql.Time
import java.time.LocalDate
import java.time.LocalTime

@Service
class SleepDataService(private val sleepDataRepository: SleepDataRepository) {
    fun createSleepData(sleepData: SleepDataPayload, user: User) {
        val seconds = timeDiff(sleepData.timeEnd, sleepData.timeStart).toSecondOfDay().toFloat()
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

    fun getLast30DaysReport(user: User): SleepDataReport {
        val startDate = Date.valueOf(LocalDate.now().minusDays(30))
        val sleepDataList = sleepDataRepository.getSleepDataSince(user.id, startDate)
        if (sleepDataList.isEmpty()) {
            throw NotFoundException("User does not have any logged sleep data")
        }

        val firstDate = sleepDataList.minByOrNull { s -> s.date } ?: throw IllegalStateException("can't happen")
        val lastDate = sleepDataList.maxByOrNull { s -> s.date } ?: throw IllegalStateException("can't happen")
        val averageSleepDuration = sleepDataList.sumOf { s -> s.durationHours.toDouble() } / sleepDataList.size
        val qualityMap = mapOf(
            SleepQuality.BAD to sleepDataList.count { it.quality == SleepQuality.BAD },
            SleepQuality.OK to sleepDataList.count { it.quality == SleepQuality.OK },
            SleepQuality.GOOD to sleepDataList.count { it.quality == SleepQuality.GOOD },
        )

        return SleepDataReport(
            startDate = firstDate.date,
            endDate = lastDate.date,
            averageSleepDuration = averageSleepDuration.toFloat(),
            averageSleepStart = averageTime(sleepDataList.map { s -> s.timeStart }, LocalTime.of(12, 0)),
            averageSleepEnd = averageTime(sleepDataList.map { s -> s.timeEnd }, LocalTime.of(0, 0)),
            qualityMap = qualityMap
        )
    }

    fun averageTime(times: List<Time>, anchorTime: LocalTime): LocalTime {
        val averageInSecondsSinceAnchor =
            times.sumOf { time -> timeDiff(time.toLocalTime(), anchorTime).toSecondOfDay() }.toFloat() / times.size
        val averageInSeconds = averageInSecondsSinceAnchor.toLong() + anchorTime.toSecondOfDay()
        return LocalTime.ofSecondOfDay(averageInSeconds)
    }

    fun timeDiff(a: LocalTime, b: LocalTime): LocalTime {
        return a.minusSeconds(b.toSecondOfDay().toLong())
    }
}
