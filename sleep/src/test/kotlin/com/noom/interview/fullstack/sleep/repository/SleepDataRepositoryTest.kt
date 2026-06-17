package com.noom.interview.fullstack.sleep.repository

import com.noom.interview.fullstack.sleep.model.SleepData
import com.noom.interview.fullstack.sleep.model.SleepQuality
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.sql.Date
import java.sql.PreparedStatement
import java.sql.Time

@ExtendWith(MockitoExtension::class)
class SleepDataRepositoryTest {
    @Mock
    private lateinit var db: DatabaseInterface

    @InjectMocks
    private lateinit var sleepDataRepository: SleepDataRepository

    @Test
    fun testCreateSleepData() {
        val sleepData = SleepData(
            id = 1,
            userId = 4,
            date = Date.valueOf("2026-01-01"),
            timeStart = Time.valueOf("18:00:00"),
            durationHours = 8,
            quality = SleepQuality.BAD
        )
        val stmt: PreparedStatement = mock()
        whenever(stmt.executeUpdate()).thenReturn(1)
        whenever(
            db.prepareStatement(
                anyString(),
                eq(4),
                eq(Date.valueOf("2026-01-01")),
                eq(Time.valueOf("18:00:00")),
                eq(8),
                eq(SleepQuality.BAD)
            )
        ).thenReturn(stmt)

        sleepDataRepository.createSleepData(sleepData)

        verify(stmt).executeUpdate()
    }


    @Test
    fun testCreateSleepDataFails() {
        val sleepData = SleepData(
            id = 1,
            userId = 4,
            date = Date.valueOf("2026-01-01"),
            timeStart = Time.valueOf("18:00:00"),
            durationHours = 8,
            quality = SleepQuality.BAD
        )
        val stmt: PreparedStatement = mock()
        whenever(stmt.executeUpdate()).thenReturn(0)
        whenever(
            db.prepareStatement(
                anyString(),
                eq(4),
                eq(Date.valueOf("2026-01-01")),
                eq(Time.valueOf("18:00:00")),
                eq(8),
                eq(SleepQuality.BAD)
            )
        ).thenReturn(stmt)

        Assertions.assertThrows(
            IllegalStateException::class.java,
            { sleepDataRepository.createSleepData(sleepData) }
        )

        verify(stmt).executeUpdate()
    }
}