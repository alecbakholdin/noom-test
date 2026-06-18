package com.noom.interview.fullstack.sleep.controller

import com.noom.interview.fullstack.sleep.auth.UserArgumentResolver
import com.noom.interview.fullstack.sleep.model.SleepDataPayload
import com.noom.interview.fullstack.sleep.model.SleepQuality
import com.noom.interview.fullstack.sleep.model.User
import com.noom.interview.fullstack.sleep.repository.UserRepository
import com.noom.interview.fullstack.sleep.service.SleepDataService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
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
        mockMvc = MockMvcBuilders
            .standaloneSetup(SleepDataController(sleepDataService))
            .setCustomArgumentResolvers(UserArgumentResolver(userRepository))
            .build()

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
                "durationHours": 8,
                "quality": "GOOD"
            }""".trimIndent()
        }.andExpect { status { isCreated() } }
        verify(
            sleepDataService
        ).createSleepData(
            SleepDataPayload(
                date = LocalDate.of(2026, 6, 17),
                timeStart = LocalTime.of(20, 0),
                durationHours = 8,
                quality = SleepQuality.GOOD
            ),
            USER
        )

    }
}