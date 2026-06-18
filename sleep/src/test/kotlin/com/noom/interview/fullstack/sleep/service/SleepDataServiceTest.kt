package com.noom.interview.fullstack.sleep.service

import com.noom.interview.fullstack.sleep.model.SleepData
import com.noom.interview.fullstack.sleep.model.SleepDataPayload
import com.noom.interview.fullstack.sleep.model.SleepQuality
import com.noom.interview.fullstack.sleep.model.User
import com.noom.interview.fullstack.sleep.repository.SleepDataRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
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
    fun testCreateSleepData() {
        sleepDataService.createSleepData(
            SleepDataPayload(
                date = LocalDate.of(2026, 1, 1),
                timeStart = LocalTime.of(18, 0),
                durationHours = 8,
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
                durationHours = 8,
                quality = SleepQuality.BAD
            )
        )
    }
}