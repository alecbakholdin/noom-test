package com.noom.interview.fullstack.sleep.config

import com.noom.interview.fullstack.sleep.auth.UserArgumentResolver
import com.noom.interview.fullstack.sleep.repository.UserRepository
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(private val userRepository: UserRepository) : WebMvcConfigurer {
    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(UserArgumentResolver(userRepository))
    }
}