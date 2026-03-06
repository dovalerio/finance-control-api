package com.heitor.finance.infrastructure.controller

import com.heitor.finance.application.dto.CategoryResponse
import com.heitor.finance.application.port.input.CreateCategoryUseCase
import com.heitor.finance.application.port.input.FindCategoryUseCase
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.assertj.MockMvcTester

class CategoryControllerTest {

    private val createCategoryUseCase: CreateCategoryUseCase = mockk()
    private val findCategoryUseCase: FindCategoryUseCase = mockk()

    private val mockMvc = MockMvcTester.of(CategoryController(createCategoryUseCase, findCategoryUseCase))

    @Test
    fun `GET categories should return 200 with list`() {
        every { findCategoryUseCase.findAll() } returns listOf(CategoryResponse(id = 1L, name = "Transport"))

        assertThat(
            mockMvc.get().uri("/v1/categorias")
        ).hasStatusOk()
    }
}
