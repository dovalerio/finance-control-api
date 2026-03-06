package com.heitor.finance.infrastructure.config

import com.heitor.finance.infrastructure.filter.ApiKeyAuthFilter
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SecurityFilterConfig(
    @Value("\${app.security.api-key}") private val apiKey: String
) {

    @Bean
    fun apiKeyFilterRegistration(): FilterRegistrationBean<ApiKeyAuthFilter> =
        FilterRegistrationBean<ApiKeyAuthFilter>(ApiKeyAuthFilter(apiKey)).apply {
            addUrlPatterns("/v1/*")
            order = 1
        }
}
