package com.noom.interview.fullstack.sleep.service

import com.noom.interview.fullstack.sleep.exception.NotFoundException
import com.noom.interview.fullstack.sleep.model.SleepData
import com.noom.interview.fullstack.sleep.model.SleepDataPayload
import com.noom.interview.fullstack.sleep.model.SleepQuality
import com.noom.interview.fullstack.sleep.model.User
import com.noom.interview.fullstack.sleep.repository.SleepDataRepository
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
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
}