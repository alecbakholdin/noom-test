package com.noom.interview.fullstack.sleep.repository

import com.noom.interview.fullstack.sleep.model.SleepData
import com.noom.interview.fullstack.sleep.model.SleepQuality
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import java.sql.Date
import java.sql.PreparedStatement
import java.sql.ResultSet
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
                eq("BAD")
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
                eq("BAD")
            )
        ).thenReturn(stmt)

        Assertions.assertThrows(
            IllegalStateException::class.java,
            { sleepDataRepository.createSleepData(sleepData) }
        )

        verify(stmt).executeUpdate()
    }

    @Test
    fun testGetLastSleep() {
        val expected = SleepData(
            id = 1,
            userId = 1,
            date = Date.valueOf("2026-01-01"),
            timeStart = Time.valueOf("18:00:00"),
            durationHours = 8,
            quality = SleepQuality.BAD
        )
        whenever(db.findOne<SleepData>(any(), anyString(), eq(4)))
            .thenReturn(expected)

        val lastSleepData = sleepDataRepository.getLastSleepData(4)
        assertEquals(expected, lastSleepData)
    }

    @Test
    fun testGetSleepDataSince() {
        val expected = listOf(
            SleepData(
                id = 1,
                userId = 1,
                date = Date.valueOf("2026-01-01"),
                timeStart = Time.valueOf("18:00:00"),
                durationHours = 8,
                quality = SleepQuality.BAD
            )
        )
        whenever(
            db.findAll<SleepData>(
                any(),
                anyString(),
                eq(4),
                eq(Date.valueOf("2026-01-01"))
            )
        ).thenReturn(expected)
        val actual = sleepDataRepository.getSleepDataSince(4, Date.valueOf("2026-01-01"))

        assertEquals(expected, actual)
    }

    @Test
    fun testDeserializeSleepData() {
        val expected = SleepData(
            id = 1,
            userId = 1,
            date = Date.valueOf("2026-01-01"),
            timeStart = Time.valueOf("18:00:00"),
            durationHours = 8,
            quality = SleepQuality.BAD
        )
        val resultSet: ResultSet = mock()
        whenever(resultSet.getInt("id")).thenReturn(1)
        whenever(resultSet.getInt("user_id")).thenReturn(1)
        whenever(resultSet.getDate("date")).thenReturn(Date.valueOf("2026-01-01"))
        whenever(resultSet.getTime("time_start")).thenReturn(Time.valueOf("18:00:00"))
        whenever(resultSet.getInt("duration_hours")).thenReturn(8)
        whenever(resultSet.getString("quality")).thenReturn("BAD")

        val actual = sleepDataRepository.deserializeSleepData(resultSet)

        assertEquals(expected, actual)
    }
}