package com.heitor.finance.infrastructure.controller

import com.heitor.finance.application.dto.EntryResponse
import com.heitor.finance.application.port.input.CreateEntryUseCase
import com.heitor.finance.application.port.input.DeleteEntryUseCase
import com.heitor.finance.application.port.input.FindEntryUseCase
import com.heitor.finance.application.port.input.UpdateEntryUseCase
import com.heitor.finance.domain.exception.EntryNotFoundException
import com.heitor.finance.domain.exception.SubcategoryNotFoundException
import com.heitor.finance.infrastructure.exception.GlobalExceptionHandler
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.assertj.MockMvcTester
import java.math.BigDecimal
import java.time.LocalDate

class EntryControllerTest {

    private val createEntryUseCase: CreateEntryUseCase = mockk()
    private val findEntryUseCase: FindEntryUseCase = mockk()
    private val updateEntryUseCase: UpdateEntryUseCase = mockk()
    private val deleteEntryUseCase: DeleteEntryUseCase = mockk()

    private val mockMvc = MockMvcTester.of(
        listOf(EntryController(createEntryUseCase, findEntryUseCase, updateEntryUseCase, deleteEntryUseCase))
    ) { it.setControllerAdvice(GlobalExceptionHandler()).build() }

    private val entryResponse = EntryResponse(
        id = 1L, value = BigDecimal("150.00"), date = LocalDate.of(2024, 3, 10),
        subcategoryId = 5L, comment = "Salary"
    )

    @Test
    fun `POST entries should return 201 with created entry`() {
        every { createEntryUseCase.execute(any()) } returns entryResponse

        assertThat(
            mockMvc.post().uri("/v1/entries")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"value":150.00,"subcategoryId":5,"date":"2024-03-10","comment":"Salary"}""")
        ).hasStatus(201)
    }

    @Test
    fun `POST entries should return 201 without optional fields`() {
        every { createEntryUseCase.execute(any()) } returns entryResponse

        assertThat(
            mockMvc.post().uri("/v1/entries")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"value":150.00,"subcategoryId":5}""")
        ).hasStatus(201)
    }

    @Test
    fun `POST entries should return 400 when body is missing required fields`() {
        assertThat(
            mockMvc.post().uri("/v1/entries")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
        ).hasStatus(400)
    }

    @Test
    fun `POST entries should return 404 when subcategory not found`() {
        every { createEntryUseCase.execute(any()) } throws SubcategoryNotFoundException(99L)

        assertThat(
            mockMvc.post().uri("/v1/entries")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"value":150.00,"subcategoryId":99}""")
        ).hasStatus(404)
    }

    @Test
    fun `GET entries should return 200 with all entries`() {
        every { findEntryUseCase.findAll(null, null, null) } returns listOf(entryResponse)

        assertThat(mockMvc.get().uri("/v1/entries")).hasStatusOk()
    }

    @Test
    fun `GET entries should forward subcategoryId filter`() {
        every { findEntryUseCase.findAll(5L, null, null) } returns listOf(entryResponse)

        assertThat(
            mockMvc.get().uri("/v1/entries").param("subcategoryId", "5")
        ).hasStatusOk()
    }

    @Test
    fun `GET entries should forward date range filters`() {
        every { findEntryUseCase.findAll(null, LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 31)) } returns listOf(entryResponse)

        assertThat(
            mockMvc.get().uri("/v1/entries")
                .param("startDate", "2024-03-01")
                .param("endDate", "2024-03-31")
        ).hasStatusOk()
    }

    @Test
    fun `GET entries by id should return 200 with entry`() {
        every { findEntryUseCase.findById(1L) } returns entryResponse

        assertThat(mockMvc.get().uri("/v1/entries/1")).hasStatusOk()
    }

    @Test
    fun `GET entries by id should return 404 when not found`() {
        every { findEntryUseCase.findById(99L) } throws EntryNotFoundException(99L)

        val result = mockMvc.get().uri("/v1/entries/99")

        assertThat(result)
            .hasStatus(404)
            .bodyJson()
            .extractingPath("$.detail")
            .asString()
            .contains("99")
    }

    @Test
    fun `PUT entries should return 200 with updated entry`() {
        every { updateEntryUseCase.execute(1L, any()) } returns entryResponse

        assertThat(
            mockMvc.put().uri("/v1/entries/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"value":200.00,"subcategoryId":5}""")
        ).hasStatusOk()
    }

    @Test
    fun `PUT entries should return 404 when entry not found`() {
        every { updateEntryUseCase.execute(99L, any()) } throws EntryNotFoundException(99L)

        assertThat(
            mockMvc.put().uri("/v1/entries/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"value":200.00,"subcategoryId":5}""")
        ).hasStatus(404)
    }

    @Test
    fun `PUT entries should return 404 when subcategory not found`() {
        every { updateEntryUseCase.execute(1L, any()) } throws SubcategoryNotFoundException(99L)

        assertThat(
            mockMvc.put().uri("/v1/entries/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"value":200.00,"subcategoryId":99}""")
        ).hasStatus(404)
    }

    @Test
    fun `DELETE entries should return 204 when deleted`() {
        every { deleteEntryUseCase.execute(1L) } returns Unit

        assertThat(mockMvc.delete().uri("/v1/entries/1")).hasStatus(204)

        verify { deleteEntryUseCase.execute(1L) }
    }

    @Test
    fun `DELETE entries should return 404 when entry not found`() {
        every { deleteEntryUseCase.execute(99L) } throws EntryNotFoundException(99L)

        assertThat(mockMvc.delete().uri("/v1/entries/99")).hasStatus(404)
    }
}
