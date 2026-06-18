package com.noom.interview.fullstack.sleep.model

import java.time.LocalDate
import java.time.LocalTime

data class SleepDataPayload(
    var date: LocalDate,
    var timeStart: LocalTime,
    var timeEnd: LocalTime,
    var quality: SleepQuality
)
