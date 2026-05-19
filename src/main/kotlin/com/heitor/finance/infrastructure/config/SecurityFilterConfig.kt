package com.heitor.finance.infrastructure.config

import com.heitor.finance.infrastructure.filter.ApiKeyAuthFilter
import com.heitor.finance.infrastructure.filter.SecurityHeadersFilter
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SecurityFilterConfig(
    @Value("\${app.security.api-key}") private val apiKey: String
) {

    @Bean
    fun securityHeadersFilterRegistration(): FilterRegistrationBean<SecurityHeadersFilter> =
        FilterRegistrationBean<SecurityHeadersFilter>(SecurityHeadersFilter()).apply {
            addUrlPatterns("/*")
            order = 0
        }

    @Bean
    fun apiKeyFilterRegistration(): FilterRegistrationBean<ApiKeyAuthFilter> =
        FilterRegistrationBean<ApiKeyAuthFilter>(ApiKeyAuthFilter(apiKey)).apply {
            // /v1/* = endpoints de negócio; /actuator/metrics* e /actuator/prometheus = métricas sensíveis
            addUrlPatterns("/v1/*", "/actuator/metrics", "/actuator/metrics/*", "/actuator/prometheus")
            order = 1
        }
}
