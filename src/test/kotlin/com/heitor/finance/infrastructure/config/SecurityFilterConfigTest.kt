package com.heitor.finance.infrastructure.config

import com.heitor.finance.infrastructure.filter.ApiKeyAuthFilter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.boot.web.servlet.FilterRegistrationBean

class SecurityFilterConfigTest {

    private val apiKey = "test-api-key"
    private val config = SecurityFilterConfig(apiKey)

    @Test
    fun `apiKeyFilterRegistration should return FilterRegistrationBean`() {
        val registration: FilterRegistrationBean<ApiKeyAuthFilter> = config.apiKeyFilterRegistration()

        assertNotNull(registration)
    }

    @Test
    fun `apiKeyFilterRegistration should have order 1`() {
        val registration = config.apiKeyFilterRegistration()

        assertEquals(1, registration.order)
    }

    @Test
    fun `apiKeyFilterRegistration should register ApiKeyAuthFilter`() {
        val registration = config.apiKeyFilterRegistration()

        assertNotNull(registration.filter)
    }

    @Test
    fun `apiKeyFilterRegistration should contain v1 url pattern`() {
        val registration = config.apiKeyFilterRegistration()

        assertNotNull(registration.urlPatterns)
        assertEquals(true, registration.urlPatterns.contains("/v1/*"))
    }
}
