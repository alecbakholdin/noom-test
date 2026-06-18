package com.noom.interview.fullstack.sleep.model

import java.sql.Date
import java.sql.Time

data class SleepData(
    var id: Int,
    var userId: Int,
    var date: Date,
    var timeStart: Time,
    var timeEnd: Time,
    var durationHours: Float,
    var quality: SleepQuality
)
