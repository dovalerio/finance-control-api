package com.heitor.finance.infrastructure.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.logging.log4j.LogManager
import org.springframework.web.filter.OncePerRequestFilter
import java.security.MessageDigest

class ApiKeyAuthFilter(
    private val expectedApiKey: String
) : OncePerRequestFilter() {

    private val log = LogManager.getLogger(ApiKeyAuthFilter::class.java)

    companion object {
        private const val API_KEY_HEADER = "api-key"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val apiKey = request.getHeader(API_KEY_HEADER)

        // MessageDigest.isEqual garante comparação em tempo constante (evita timing attack)
        val valid = apiKey != null && MessageDigest.isEqual(
            apiKey.toByteArray(Charsets.UTF_8),
            expectedApiKey.toByteArray(Charsets.UTF_8)
        )

        if (!valid) {
            log.warn(
                "Unauthorized request method={} uri={} remoteAddr={} reason={}",
                request.method,
                request.requestURI,
                request.remoteAddr,
                if (apiKey == null) "missing api-key" else "invalid api-key"
            )
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.contentType = "application/json"
            response.writer.write("""{"codigo":"nao_autorizado","mensagem":"api-key ausente ou inválida"}""")
            return
        }

        log.debug("Authorized request method={} uri={}", request.method, request.requestURI)
        filterChain.doFilter(request, response)
    }
}
