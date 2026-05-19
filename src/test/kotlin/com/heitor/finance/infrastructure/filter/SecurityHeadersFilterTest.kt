package com.heitor.finance.infrastructure.filter

import jakarta.servlet.FilterChain
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

class SecurityHeadersFilterTest {

    private val filter = SecurityHeadersFilter()

    @Test
    fun `should add X-Content-Type-Options header`() {
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()
        val chain = mock(FilterChain::class.java)

        filter.doFilter(request, response, chain)

        assertEquals("nosniff", response.getHeader("X-Content-Type-Options"))
    }

    @Test
    fun `should add X-Frame-Options header`() {
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()
        val chain = mock(FilterChain::class.java)

        filter.doFilter(request, response, chain)

        assertEquals("DENY", response.getHeader("X-Frame-Options"))
    }

    @Test
    fun `should add Cache-Control header`() {
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()
        val chain = mock(FilterChain::class.java)

        filter.doFilter(request, response, chain)

        assertEquals("no-store", response.getHeader("Cache-Control"))
    }

    @Test
    fun `should pass request to next filter in chain`() {
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()
        val chain = mock(FilterChain::class.java)

        filter.doFilter(request, response, chain)

        verify(chain).doFilter(request, response)
    }
}
