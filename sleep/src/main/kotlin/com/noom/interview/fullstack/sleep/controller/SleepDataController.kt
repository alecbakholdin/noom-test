package com.noom.interview.fullstack.sleep.controller

import com.noom.interview.fullstack.sleep.model.SleepData
import com.noom.interview.fullstack.sleep.model.SleepDataPayload
import com.noom.interview.fullstack.sleep.model.SleepDataReport
import com.noom.interview.fullstack.sleep.model.User
import com.noom.interview.fullstack.sleep.service.SleepDataService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class SleepDataController(
    private var sleepDataService: SleepDataService
) {
    @PostMapping("/api/sleep/log")
    fun logSleepData(@RequestBody sleepData: SleepDataPayload, user: User): ResponseEntity<Void> {
        sleepDataService.createSleepData(sleepData, user)
        return ResponseEntity<Void>(HttpStatus.CREATED)
    }

    @GetMapping("/api/sleep/log")
    fun getLastSleepData(user: User): SleepData {
        return sleepDataService.getLastSleep(user)
    }

    @GetMapping("/api/sleep/report")
    fun getLast30DaysReport(user: User): SleepDataReport {
        return sleepDataService.getLast30DaysReport(user)
    }
}