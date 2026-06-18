package com.noom.interview.fullstack.sleep.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.UNAUTHORIZED, reason = "Please provide an X-User-Name header")
class UnauthorizedException(message: String) : RuntimeException(message) {
}