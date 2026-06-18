package com.noom.interview.fullstack.sleep.service

import com.noom.interview.fullstack.sleep.exception.NotFoundException
import com.noom.interview.fullstack.sleep.model.*
import com.noom.interview.fullstack.sleep.repository.SleepDataRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.sql.Date
import java.sql.Time
import java.time.LocalDate
import java.time.LocalTime

@ExtendWith(MockitoExtension::class)
class SleepDataServiceTest {
    @Mock
    private lateinit var sleepDataRepository: SleepDataRepository

    @InjectMocks
    private lateinit var sleepDataService: SleepDataService

    @Test
    fun testCreateSleepDataInvertedTimes() {
        sleepDataService.createSleepData(
            SleepDataPayload(
                date = LocalDate.of(2026, 1, 1),
                timeStart = LocalTime.of(18, 0),
                timeEnd = LocalTime.of(4, 0),
                quality = SleepQuality.BAD
            ),
            User(id = 2, username = "user")
        )

        verify(sleepDataRepository).createSleepData(
            SleepData(
                id = 0,
                userId = 2,
                date = Date.valueOf("2026-01-01"),
                timeStart = Time.valueOf("18:00:00"),
                timeEnd = Time.valueOf("4:00:00"),
                durationHours = 10f,
                quality = SleepQuality.BAD
            )
        )
    }

    @Test
    fun testCreateSleepDataRegularTimes() {
        sleepDataService.createSleepData(
            SleepDataPayload(
                date = LocalDate.of(2026, 1, 1),
                timeStart = LocalTime.of(4, 0),
                timeEnd = LocalTime.of(12, 0),
                quality = SleepQuality.BAD
            ),
            User(id = 2, username = "user")
        )

        verify(sleepDataRepository).createSleepData(
            SleepData(
                id = 0,
                userId = 2,
                date = Date.valueOf("2026-01-01"),
                timeStart = Time.valueOf("4:00:00"),
                timeEnd = Time.valueOf("12:00:00"),
                durationHours = 8f,
                quality = SleepQuality.BAD
            )
        )
    }

    @Test
    fun testCreateSleepDataFractionalTimes() {
        sleepDataService.createSleepData(
            SleepDataPayload(
                date = LocalDate.of(2026, 1, 1),
                timeStart = LocalTime.of(4, 0),
                timeEnd = LocalTime.of(12, 30),
                quality = SleepQuality.BAD
            ),
            User(id = 2, username = "user")
        )

        verify(sleepDataRepository).createSleepData(
            SleepData(
                id = 0,
                userId = 2,
                date = Date.valueOf("2026-01-01"),
                timeStart = Time.valueOf("4:00:00"),
                timeEnd = Time.valueOf("12:30:00"),
                durationHours = 8.5f,
                quality = SleepQuality.BAD
            )
        )
    }

    @Test
    fun testGetLastSleepData() {
        val expected = SleepData(
            id = 0,
            userId = 2,
            date = Date.valueOf("2026-01-01"),
            timeStart = Time.valueOf("18:00:00"),
            timeEnd = Time.valueOf("4:00:00"),
            durationHours = 10f,
            quality = SleepQuality.BAD
        )

        whenever(sleepDataRepository.getLastSleepData(1)).thenReturn(expected)
        val actual = sleepDataService.getLastSleep(User(id = 1, username = "user"))
        assertSame(expected, actual)
    }

    @Test
    fun testGetLastSleepDataDoesntExist() {
        whenever(sleepDataRepository.getLastSleepData(1)).thenReturn(null)
        assertThrows(NotFoundException::class.java) {
            sleepDataService.getLastSleep(User(id = 1, username = "user"))
        }
    }

    @Test
    fun testLast30DayReportCalculatesProperly() {
        whenever(sleepDataRepository.getSleepDataSince(anyInt(), any())).thenReturn(
            listOf(
                SleepData(
                    id = 1,
                    userId = 1,
                    date = Date.valueOf("2026-01-01"),
                    timeStart = Time.valueOf("23:00:00"),
                    timeEnd = Time.valueOf("07:00:00"),
                    durationHours = 8f,
                    quality = SleepQuality.BAD
                ),
                SleepData(
                    id = 2,
                    userId = 1,
                    date = Date.valueOf("2026-01-02"),
                    timeStart = Time.valueOf("00:00:00"),
                    timeEnd = Time.valueOf("9:00:00"),
                    durationHours = 9f,
                    quality = SleepQuality.OK
                )
            )
        )

        val report = sleepDataService.getLast30DaysReport(User(id = 1, username = "user"))

        assertEquals(
            SleepDataReport(
                startDate = Date.valueOf("2026-01-01"),
                endDate = Date.valueOf("2026-01-02"),
                averageSleepDuration = 8.5f,
                averageSleepStart = LocalTime.of(23, 30),
                averageSleepEnd = LocalTime.of(8, 0),
                qualityMap = mapOf(
                    SleepQuality.BAD to 1,
                    SleepQuality.OK to 1,
                    SleepQuality.GOOD to 0
                )
            ),
            report
        )
    }

    @Test
    fun testLast30DaysReportNoDataThrowsNotFound() {
        whenever(sleepDataRepository.getSleepDataSince(anyInt(), any())).thenReturn(listOf())

        assertThrows(NotFoundException::class.java, {
            sleepDataService.getLast30DaysReport(User(id = 1, username = "user"))
        })
    }

    @Test
    fun testAverageTime() {
        runAvgTimeTest("10:00", listOf("10:00"), "10:00")
        runAvgTimeTest("10:00", listOf("10:00"), "5:00")
        runAvgTimeTest("7:30", listOf("10:00", "5:00"), "5:00")
        runAvgTimeTest("7:30", listOf("10:00", "5:00"), "3:00")
        runAvgTimeTest("11:30", listOf("23:00", "0:00"), "0:00")
        runAvgTimeTest("23:30", listOf("23:00", "0:00"), "22:00")
    }

    fun runAvgTimeTest(expected: String, timeStrs: List<String>, anchor: String) {
        val expectedTime = Time.valueOf("$expected:00").toLocalTime()
        val anchorTime = Time.valueOf("$anchor:00").toLocalTime()
        val times = timeStrs.map { t -> Time.valueOf("$t:00") }
        assertEquals(expectedTime, sleepDataService.averageTime(times, anchorTime))
    }

}