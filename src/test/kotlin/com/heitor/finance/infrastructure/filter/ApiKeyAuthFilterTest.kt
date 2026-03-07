package com.heitor.finance.infrastructure.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

class ApiKeyAuthFilterTest {

    private val filter = ApiKeyAuthFilter("aXRhw7o=")

    @Test
    fun `should allow request with valid api-key`() {
        val request = MockHttpServletRequest().apply { addHeader("api-key", "aXRhw7o=") }
        val response = MockHttpServletResponse()
        val chain = mock(FilterChain::class.java)

        filter.doFilter(request, response, chain)

        verify(chain).doFilter(request, response)
    }

    @Test
    fun `should return 401 when api-key is missing`() {
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()
        val chain = mock(FilterChain::class.java)

        filter.doFilter(request, response, chain)

        assert(response.status == HttpServletResponse.SC_UNAUTHORIZED)
        verifyNoInteractions(chain)
    }

    @Test
    fun `should return 401 when api-key is present but invalid`() {
        val request = MockHttpServletRequest().apply { addHeader("api-key", "wrong-key") }
        val response = MockHttpServletResponse()
        val chain = mock(FilterChain::class.java)

        filter.doFilter(request, response, chain)

        assert(response.status == HttpServletResponse.SC_UNAUTHORIZED)
        verifyNoInteractions(chain)
    }

    @Test
    fun `should return 401 with json content type when api-key is missing`() {
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()
        val chain = mock(FilterChain::class.java)

        filter.doFilter(request, response, chain)

        assert(response.contentType == "application/json")
        assert(response.contentAsString.contains("nao_autorizado"))
    }

    @Test
    fun `should not pass request to chain when api-key is invalid`() {
        val request = MockHttpServletRequest().apply { addHeader("api-key", "aXRhw7o") } // missing =
        val response = MockHttpServletResponse()
        val chain = mock(FilterChain::class.java)

        filter.doFilter(request, response, chain)

        assert(response.status == HttpServletResponse.SC_UNAUTHORIZED)
        verify(chain, never()).doFilter(any(), any())
    }
}
