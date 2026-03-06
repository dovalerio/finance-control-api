package com.heitor.finance.infrastructure.controller

import com.heitor.finance.application.dto.CategoryResponse
import com.heitor.finance.application.port.input.CreateCategoryUseCase
import com.heitor.finance.application.port.input.DeleteCategoryUseCase
import com.heitor.finance.application.port.input.FindCategoryUseCase
import com.heitor.finance.application.port.input.UpdateCategoryUseCase
import com.heitor.finance.domain.exception.CategoryAlreadyExistsException
import com.heitor.finance.domain.exception.CategoryNotFoundException
import com.heitor.finance.infrastructure.exception.GlobalExceptionHandler
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.assertj.MockMvcTester

class CategoryControllerTest {

    private val createCategoryUseCase: CreateCategoryUseCase = mockk()
    private val findCategoryUseCase: FindCategoryUseCase = mockk()
    private val updateCategoryUseCase: UpdateCategoryUseCase = mockk()
    private val deleteCategoryUseCase: DeleteCategoryUseCase = mockk()

    private val mockMvc = MockMvcTester.of(
        listOf(CategoryController(createCategoryUseCase, findCategoryUseCase, updateCategoryUseCase, deleteCategoryUseCase))
    ) { it.setControllerAdvice(GlobalExceptionHandler()).build() }

    @Test
    fun `POST categories should return 201 with created category`() {
        every { createCategoryUseCase.execute(any()) } returns CategoryResponse(id = 1L, name = "Transport")

        assertThat(
            mockMvc.post().uri("/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name":"Transport"}""")
        ).hasStatus(201)
    }

    @Test
    fun `POST categories should return 400 when name is blank`() {
        assertThat(
            mockMvc.post().uri("/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name":""}""")
        ).hasStatus(400)
    }

    @Test
    fun `POST categories should return 409 when category already exists`() {
        every { createCategoryUseCase.execute(any()) } throws CategoryAlreadyExistsException("Transport")

        assertThat(
            mockMvc.post().uri("/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name":"Transport"}""")
        ).hasStatus(409)
    }

    @Test
    fun `GET categories should return 200 with full list when no filter`() {
        every { findCategoryUseCase.findAll(null) } returns listOf(CategoryResponse(id = 1L, name = "Transport"))

        assertThat(
            mockMvc.get().uri("/v1/categories")
        ).hasStatusOk()
    }

    @Test
    fun `GET categories should forward name filter to use case`() {
        every { findCategoryUseCase.findAll("trans") } returns listOf(CategoryResponse(id = 1L, name = "Transport"))

        assertThat(
            mockMvc.get().uri("/v1/categories").param("name", "trans")
        ).hasStatusOk()
    }

    @Test
    fun `GET categories by id should return 200 with category`() {
        every { findCategoryUseCase.findById(1L) } returns CategoryResponse(id = 1L, name = "Transport")

        assertThat(
            mockMvc.get().uri("/v1/categories/1")
        ).hasStatusOk()
    }

    @Test
    fun `GET categories by id should return 404 when not found`() {
        every { findCategoryUseCase.findById(99L) } throws CategoryNotFoundException(99L)

        assertThat(
            mockMvc.get().uri("/v1/categories/99")
        ).hasStatus(404)
    }

    @Test
    fun `PUT categories should return 200 with updated category`() {
        every { updateCategoryUseCase.execute(1L, any()) } returns CategoryResponse(id = 1L, name = "Food")

        assertThat(
            mockMvc.put().uri("/v1/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name":"Food"}""")
        ).hasStatusOk()
    }

    @Test
    fun `PUT categories should return 400 when name is blank`() {
        assertThat(
            mockMvc.put().uri("/v1/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name":""}""")
        ).hasStatus(400)
    }

    @Test
    fun `PUT categories should return 404 when category not found`() {
        every { updateCategoryUseCase.execute(99L, any()) } throws CategoryNotFoundException(99L)

        assertThat(
            mockMvc.put().uri("/v1/categories/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name":"Food"}""")
        ).hasStatus(404)
    }

    @Test
    fun `PUT categories should return 409 when name already taken by another category`() {
        every { updateCategoryUseCase.execute(1L, any()) } throws CategoryAlreadyExistsException("Food")

        assertThat(
            mockMvc.put().uri("/v1/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name":"Food"}""")
        ).hasStatus(409)
    }

    @Test
    fun `DELETE categories should return 204 when category exists`() {
        every { deleteCategoryUseCase.execute(1L) } returns Unit

        assertThat(
            mockMvc.delete().uri("/v1/categories/1")
        ).hasStatus(204)
    }

    @Test
    fun `DELETE categories should return 404 when category not found`() {
        every { deleteCategoryUseCase.execute(99L) } throws CategoryNotFoundException(99L)

        assertThat(
            mockMvc.delete().uri("/v1/categories/99")
        ).hasStatus(404)
    }
}
