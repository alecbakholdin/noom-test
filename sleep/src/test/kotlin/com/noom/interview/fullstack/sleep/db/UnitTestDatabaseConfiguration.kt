package com.noom.interview.fullstack.sleep.db

import com.noom.interview.fullstack.sleep.SleepApplication.Companion.UNIT_TEST_PROFILE
import org.mockito.kotlin.mock
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.sql.Connection


@Configuration
@Profile("$UNIT_TEST_PROFILE")
class UnitTestDatabaseConfiguration {
    @Bean
    fun connection(): Connection {
        return mock()
    }
}