package com.heitor.finance.infrastructure.config

import com.heitor.finance.infrastructure.filter.ApiKeyAuthFilter
import com.heitor.finance.infrastructure.filter.SecurityHeadersFilter
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

    @Test
    fun `apiKeyFilterRegistration should protect actuator metrics endpoint`() {
        val registration = config.apiKeyFilterRegistration()

        assertEquals(true, registration.urlPatterns.contains("/actuator/metrics"))
        assertEquals(true, registration.urlPatterns.contains("/actuator/metrics/*"))
        assertEquals(true, registration.urlPatterns.contains("/actuator/prometheus"))
    }

    @Test
    fun `securityHeadersFilterRegistration should return FilterRegistrationBean with order 0`() {
        val registration: FilterRegistrationBean<SecurityHeadersFilter> = config.securityHeadersFilterRegistration()

        assertNotNull(registration)
        assertEquals(0, registration.order)
        assertNotNull(registration.filter)
    }

    @Test
    fun `securityHeadersFilterRegistration should apply to all url patterns`() {
        val registration = config.securityHeadersFilterRegistration()

        assertEquals(true, registration.urlPatterns.contains("/*"))
    }
}
