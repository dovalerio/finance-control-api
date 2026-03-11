package com.heitor.finance.infrastructure.controller

import com.heitor.finance.application.dto.BalanceResponse
import com.heitor.finance.application.dto.CategoryResponse
import com.heitor.finance.application.port.input.FindBalanceUseCase
import com.heitor.finance.domain.exception.CategoryNotFoundException
import com.heitor.finance.infrastructure.exception.GlobalExceptionHandler
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.assertj.MockMvcTester
import java.math.BigDecimal
import java.time.LocalDate

class BalanceControllerTest {

    private val findBalanceUseCase: FindBalanceUseCase = mockk()

    private val mockMvc = MockMvcTester.of(
        listOf(BalanceController(findBalanceUseCase))
    ) { it.setControllerAdvice(GlobalExceptionHandler()).build() }

    @Test
    fun `GET balanco should return 200 with category when id_categoria provided`() {
        every { findBalanceUseCase.findByPeriodAndCategory(any(), any(), 1L) } returns BalanceResponse(
            category = CategoryResponse(id = 1L, name = "Transport"),
            revenue = BigDecimal("200.00"),
            expense = BigDecimal("100.00"),
            balance = BigDecimal("100.00")
        )

        assertThat(
            mockMvc.get().uri("/v1/balanco")
                .param("data_inicio", "01/01/2024")
                .param("data_fim", "31/01/2024")
                .param("id_categoria", "1")
        ).hasStatusOk()
    }

    @Test
    fun `GET balanco should return 200 without category when id_categoria not provided`() {
        every { findBalanceUseCase.findByPeriodAndCategory(any(), any(), null) } returns BalanceResponse(
            category = null,
            revenue = BigDecimal("500.00"),
            expense = BigDecimal("150.00"),
            balance = BigDecimal("350.00")
        )

        assertThat(
            mockMvc.get().uri("/v1/balanco")
                .param("data_inicio", "01/01/2024")
                .param("data_fim", "31/01/2024")
        ).hasStatusOk()
    }

    @Test
    fun `GET balanco should return 404 when category not found`() {
        every { findBalanceUseCase.findByPeriodAndCategory(any(), any(), 99L) } throws CategoryNotFoundException(99L)

        assertThat(
            mockMvc.get().uri("/v1/balanco")
                .param("data_inicio", "01/01/2024")
                .param("data_fim", "31/01/2024")
                .param("id_categoria", "99")
        ).hasStatus(404)
    }

    @Test
    fun `GET balanco should return 400 when data_inicio is after data_fim`() {
        every { findBalanceUseCase.findByPeriodAndCategory(any(), any(), null) } throws
            com.heitor.finance.domain.exception.InvalidPeriodException("Start date must not be after end date")

        assertThat(
            mockMvc.get().uri("/v1/balanco")
                .param("data_inicio", "01/02/2024")
                .param("data_fim", "01/01/2024")
        ).hasStatus(400)
    }

    @Test
    fun `GET balanco should return 400 when data_inicio is missing`() {
        assertThat(
            mockMvc.get().uri("/v1/balanco")
                .param("data_fim", "31/01/2024")
        ).hasStatus(400)
    }

    @Test
    fun `GET balanco should return 400 when data_fim is missing`() {
        assertThat(
            mockMvc.get().uri("/v1/balanco")
                .param("data_inicio", "01/01/2024")
        ).hasStatus(400)
    }

    @Test
    fun `GET balanco should return 400 with error body for invalid period`() {
        every { findBalanceUseCase.findByPeriodAndCategory(any(), any(), null) } throws
            com.heitor.finance.domain.exception.InvalidPeriodException("Start date must not be after end date")

        val result = mockMvc.get().uri("/v1/balanco")
            .param("data_inicio", "01/02/2024")
            .param("data_fim", "01/01/2024")

        assertThat(result)
            .hasStatus(400)
            .bodyJson()
            .extractingPath("$.mensagem")
            .asString()
            .contains("Start date")
    }

    @Test
    fun `GET balanco should serialize receita despesa saldo as strings`() {
        every { findBalanceUseCase.findByPeriodAndCategory(any(), any(), null) } returns BalanceResponse(
            category = null,
            revenue = BigDecimal("2320"),
            expense = BigDecimal("1000"),
            balance = BigDecimal("1320")
        )

        val result = mockMvc.get().uri("/v1/balanco")
            .param("data_inicio", "01/01/2021")
            .param("data_fim", "31/01/2021")

        assertThat(result).hasStatusOk()
        assertThat(result).bodyJson().extractingPath("$.receita").asString().isEqualTo("2320.00")
        assertThat(result).bodyJson().extractingPath("$.despesa").asString().isEqualTo("1000.00")
        assertThat(result).bodyJson().extractingPath("$.saldo").asString().isEqualTo("1320.00")
    }
}
