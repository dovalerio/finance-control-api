package com.heitor.finance.infrastructure.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter

class ApiKeyAuthFilter(
    private val expectedApiKey: String
) : OncePerRequestFilter() {

    companion object {
        private const val API_KEY_HEADER = "api-key"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val apiKey = request.getHeader(API_KEY_HEADER)

        if (apiKey == null || apiKey != expectedApiKey) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.contentType = "application/json"
            response.writer.write("""{"status":401,"title":"Unauthorized","detail":"Invalid or missing api-key header"}""")
            return
        }

        filterChain.doFilter(request, response)
    }
}
