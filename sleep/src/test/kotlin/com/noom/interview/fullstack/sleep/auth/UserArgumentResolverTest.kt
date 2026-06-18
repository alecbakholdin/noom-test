package com.noom.interview.fullstack.sleep.auth

import com.noom.interview.fullstack.sleep.model.User
import com.noom.interview.fullstack.sleep.repository.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.core.MethodParameter
import org.springframework.web.context.request.NativeWebRequest

@ExtendWith(MockitoExtension::class)
class UserArgumentResolverTest {
    @Mock
    private lateinit var userRepository: UserRepository

    @InjectMocks
    private lateinit var userArgumentResolver: UserArgumentResolver

    @Test
    fun testShouldResolveProperly() {
        val method = UserArgumentResolverTest::class.java.getMethod("testMethod", User::class.java, String::class.java)
        assertTrue(userArgumentResolver.supportsParameter(MethodParameter(method, 0)))
        assertFalse(userArgumentResolver.supportsParameter(MethodParameter(method, 1)))
    }

    @Test
    fun testShouldLoadUserIfPresent() {
        val user = User(id = 4, username = "user")
        whenever(userRepository.getUserByUsername("user")).thenReturn(user)
        val method = UserArgumentResolverTest::class.java.getMethod("testMethod", User::class.java, String::class.java)
        val webRequest: NativeWebRequest = mock()
        whenever(webRequest.getHeader("X-User-Name")).thenReturn("user")

        val userResponse = userArgumentResolver.resolveArgument(MethodParameter(method, 0), null, webRequest, null)

        assertEquals(user, userResponse)
        verify(userRepository, never()).createUser(any())
    }

    @Test
    fun testShouldLoadAndCreateUserIfNotPresent() {
        val user = User(id = 4, username = "user")
        whenever(userRepository.getUserByUsername("user")).thenReturn(null).thenReturn(user)
        val method = UserArgumentResolverTest::class.java.getMethod("testMethod", User::class.java, String::class.java)
        val webRequest: NativeWebRequest = mock()
        whenever(webRequest.getHeader("X-User-Name")).thenReturn("user")

        val userResponse = userArgumentResolver.resolveArgument(MethodParameter(method, 0), null, webRequest, null)

        assertEquals(user, userResponse)
        verify(userRepository).createUser(any())
    }

    @SuppressWarnings
    fun testMethod(user: User, test: String) {

    }
}