package com.noom.interview.fullstack.sleep.auth

import com.noom.interview.fullstack.sleep.exception.UnauthorizedException
import com.noom.interview.fullstack.sleep.model.User
import com.noom.interview.fullstack.sleep.repository.UserRepository
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer


class UserArgumentResolver(private val userRepository: UserRepository) : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == User::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): User {
        val username = webRequest.getHeader("X-User-Name")
            ?: throw UnauthorizedException("Missing X-User-Name header")

        val user = userRepository.getUserByUsername(username)
        if (user == null) {
            userRepository.createUser(User(id = 0, username = username))
        }
        return userRepository.getUserByUsername(username) ?: throw UnauthorizedException("Failed to create user")
    }
}