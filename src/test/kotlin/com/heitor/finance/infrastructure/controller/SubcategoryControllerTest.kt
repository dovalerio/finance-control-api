package com.heitor.finance.infrastructure.controller

import com.heitor.finance.application.dto.SubcategoryResponse
import com.heitor.finance.application.port.input.CreateSubcategoryUseCase
import com.heitor.finance.application.port.input.DeleteSubcategoryUseCase
import com.heitor.finance.application.port.input.FindSubcategoryUseCase
import com.heitor.finance.application.port.input.UpdateSubcategoryUseCase
import com.heitor.finance.domain.exception.CategoryNotFoundException
import com.heitor.finance.domain.exception.SubcategoryAlreadyExistsException
import com.heitor.finance.domain.exception.SubcategoryNotFoundException
import com.heitor.finance.infrastructure.exception.GlobalExceptionHandler
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.assertj.MockMvcTester

class SubcategoryControllerTest {

    private val createSubcategoryUseCase: CreateSubcategoryUseCase = mockk()
    private val findSubcategoryUseCase: FindSubcategoryUseCase = mockk()
    private val updateSubcategoryUseCase: UpdateSubcategoryUseCase = mockk()
    private val deleteSubcategoryUseCase: DeleteSubcategoryUseCase = mockk()

    private val mockMvc = MockMvcTester.of(
        listOf(SubcategoryController(createSubcategoryUseCase, findSubcategoryUseCase, updateSubcategoryUseCase, deleteSubcategoryUseCase))
    ) { it.setControllerAdvice(GlobalExceptionHandler()).build() }

    private val subcategoryResponse = SubcategoryResponse(id = 5L, name = "Fuel", categoryId = 2L)

    @Test
    fun `POST subcategorias should return 201 with created subcategory`() {
        every { createSubcategoryUseCase.execute(any()) } returns subcategoryResponse

        assertThat(
            mockMvc.post().uri("/v1/subcategorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"nome":"Fuel","id_categoria":2}""")
        ).hasStatus(201)
    }

    @Test
    fun `POST subcategorias should return 400 when body is malformed`() {
        assertThat(
            mockMvc.post().uri("/v1/subcategorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
        ).hasStatus(400)
    }

    @Test
    fun `POST subcategorias should return 409 when subcategory already exists`() {
        every { createSubcategoryUseCase.execute(any()) } throws SubcategoryAlreadyExistsException("Fuel", 2L)

        val result = mockMvc.post().uri("/v1/subcategorias")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""{"nome":"Fuel","id_categoria":2}""")

        assertThat(result)
            .hasStatus(409)
            .bodyJson()
            .extractingPath("$.mensagem")
            .asString()
            .contains("Fuel")
    }

    @Test
    fun `POST subcategorias should return 404 when category not found`() {
        every { createSubcategoryUseCase.execute(any()) } throws CategoryNotFoundException(99L)

        assertThat(
            mockMvc.post().uri("/v1/subcategorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"nome":"Fuel","id_categoria":99}""")
        ).hasStatus(404)
    }

    @Test
    fun `GET subcategorias should return 200 with full list`() {
        every { findSubcategoryUseCase.findAll(null, null) } returns listOf(subcategoryResponse)

        assertThat(mockMvc.get().uri("/v1/subcategorias")).hasStatusOk()
    }

    @Test
    fun `GET subcategorias should forward nome filter`() {
        every { findSubcategoryUseCase.findAll("Fuel", null) } returns listOf(subcategoryResponse)

        assertThat(
            mockMvc.get().uri("/v1/subcategorias").param("nome", "Fuel")
        ).hasStatusOk()
    }

    @Test
    fun `GET subcategorias should forward id_subcategoria filter`() {
        every { findSubcategoryUseCase.findAll(null, 5L) } returns listOf(subcategoryResponse)

        assertThat(
            mockMvc.get().uri("/v1/subcategorias").param("id_subcategoria", "5")
        ).hasStatusOk()
    }

    @Test
    fun `GET subcategorias by id should return 200 with subcategory`() {
        every { findSubcategoryUseCase.findById(5L) } returns subcategoryResponse

        assertThat(mockMvc.get().uri("/v1/subcategorias/5")).hasStatusOk()
    }

    @Test
    fun `GET subcategorias by id should return 404 when not found`() {
        every { findSubcategoryUseCase.findById(99L) } throws SubcategoryNotFoundException(99L)

        val result = mockMvc.get().uri("/v1/subcategorias/99")

        assertThat(result)
            .hasStatus(404)
            .bodyJson()
            .extractingPath("$.mensagem")
            .asString()
            .contains("99")
    }

    @Test
    fun `PUT subcategorias should return 200 with updated subcategory`() {
        every { updateSubcategoryUseCase.execute(5L, any()) } returns subcategoryResponse

        assertThat(
            mockMvc.put().uri("/v1/subcategorias/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"nome":"Fuel","id_categoria":2}""")
        ).hasStatusOk()
    }

    @Test
    fun `PUT subcategorias should return 404 when not found`() {
        every { updateSubcategoryUseCase.execute(99L, any()) } throws SubcategoryNotFoundException(99L)

        assertThat(
            mockMvc.put().uri("/v1/subcategorias/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"nome":"Fuel","id_categoria":2}""")
        ).hasStatus(404)
    }

    @Test
    fun `PUT subcategorias should return 409 when name conflict`() {
        every { updateSubcategoryUseCase.execute(5L, any()) } throws SubcategoryAlreadyExistsException("Fuel", 2L)

        assertThat(
            mockMvc.put().uri("/v1/subcategorias/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"nome":"Fuel","id_categoria":2}""")
        ).hasStatus(409)
    }

    @Test
    fun `DELETE subcategorias should return 204 when deleted`() {
        every { deleteSubcategoryUseCase.execute(5L) } returns Unit

        assertThat(mockMvc.delete().uri("/v1/subcategorias/5")).hasStatus(204)

        verify { deleteSubcategoryUseCase.execute(5L) }
    }

    @Test
    fun `DELETE subcategorias should return 404 when not found`() {
        every { deleteSubcategoryUseCase.execute(99L) } throws SubcategoryNotFoundException(99L)

        assertThat(mockMvc.delete().uri("/v1/subcategorias/99")).hasStatus(404)
    }
}
