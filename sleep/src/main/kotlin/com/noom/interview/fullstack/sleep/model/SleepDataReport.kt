package com.noom.interview.fullstack.sleep.model

import java.sql.Date
import java.time.LocalTime

data class SleepDataReport(
    val startDate: Date,
    val endDate: Date,
    val averageSleepDuration: Float,
    val averageSleepStart: LocalTime,
    val averageSleepEnd: LocalTime,
    val qualityMap: Map<SleepQuality, Int>
) {
}