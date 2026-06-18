package com.noom.interview.fullstack.sleep.controller

import com.noom.interview.fullstack.sleep.model.*
import com.noom.interview.fullstack.sleep.repository.UserRepository
import com.noom.interview.fullstack.sleep.service.SleepDataService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.sql.Date
import java.sql.Time
import java.time.LocalDate
import java.time.LocalTime

@WebMvcTest(SleepDataController::class)
class SleepDataControllerTest {
    companion object {
        const val USERNAME = "username"
        const val USER_ID = 2
        val USER = User(id = USER_ID, username = USERNAME)
    }

    @Autowired
    private lateinit var context: WebApplicationContext

    @MockBean
    private lateinit var sleepDataService: SleepDataService

    @MockBean
    private lateinit var userRepository: UserRepository

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build()

        whenever(userRepository.getUserByUsername(USERNAME)).thenReturn(USER)
    }

    @Test
    fun `should log sleep data for user`() {
        mockMvc.post("/api/sleep/log") {
            header("X-User-Name", USERNAME)
            contentType = MediaType.APPLICATION_JSON
            content = """{
                "date": "2026-06-17",
                "timeStart": "20:00:00",
                "timeEnd": "04:00:00",
                "quality": "GOOD"
            }""".trimIndent()
        }.andExpect { status { isCreated() } }
        verify(
            sleepDataService
        ).createSleepData(
            SleepDataPayload(
                date = LocalDate.of(2026, 6, 17),
                timeStart = LocalTime.of(20, 0),
                timeEnd = LocalTime.of(4, 0),
                quality = SleepQuality.GOOD
            ),
            USER
        )

    }

    @Test
    fun `should fetch last sleep data from service`() {
        SleepData(
            id = 1,
            userId = USER_ID,
            date = Date.valueOf("2026-01-01"),
            timeStart = Time.valueOf("18:00:00"),
            timeEnd = Time.valueOf("4:00:00"),
            durationHours = 10f,
            quality = SleepQuality.BAD
        )
        mockMvc.get("/api/sleep/log") {
            header("X-User-Name", USERNAME)
        }.andExpect {
            status { isOk() }
            content {
                contentType(MediaType.APPLICATION_JSON)
                json(
                    """{
                |  "id": 1,
                |  "userId": $USER_ID,
                |  "date": "2026-01-01",
                |  "timeStart": "18:00:00",
                |  "durationHours": 8,
                |  "quality": "BAD"
                }""".trimMargin()
                )
            }
        }

    }

    @Test
    fun `should fetch last 30 days report`() {
        val sleepData = SleepDataReport(
            startDate = Date.valueOf("2026-06-11"),
            endDate = Date.valueOf("2026-06-17"),
            averageSleepDuration = 8.375f,
            averageSleepStart = LocalTime.of(20, 30),
            averageSleepEnd = LocalTime.of(4, 52, 30),
            qualityMap = mapOf(
                SleepQuality.BAD to 0,
                SleepQuality.OK to 2,
                SleepQuality.GOOD to 6

            )
        )
        whenever(sleepDataService.getLast30DaysReport(any())).thenReturn(sleepData)
        mockMvc.get("/api/sleep/report") {
            header("X-User-Name", USERNAME)
        }.andExpect {
            status { isOk() }
            content {
                contentType(MediaType.APPLICATION_JSON)
                json(
                    """{
                        "startDate": "2026-06-11",
                        "endDate": "2026-06-17",
                        "averageSleepDuration": 8.375,
                        "averageSleepStart": "20:30:00",
                        "averageSleepEnd": "04:52:30",
                        "qualityMap": {
                          "BAD": 0,
                          "OK": 2,
                          "GOOD": 6
                        }
                    }""".trimIndent()
                )
            }
        }
    }
}