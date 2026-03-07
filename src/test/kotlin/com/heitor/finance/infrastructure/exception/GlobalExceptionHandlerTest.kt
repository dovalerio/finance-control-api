package com.heitor.finance.infrastructure.exception

import com.heitor.finance.domain.exception.CategoryAlreadyExistsException
import com.heitor.finance.domain.exception.CategoryNotFoundException
import com.heitor.finance.domain.exception.EntryNotFoundException
import com.heitor.finance.domain.exception.InvalidPeriodException
import com.heitor.finance.domain.exception.SubcategoryAlreadyExistsException
import com.heitor.finance.domain.exception.SubcategoryNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.mock.http.MockHttpInputMessage
import org.springframework.web.bind.MissingServletRequestParameterException

class GlobalExceptionHandlerTest {

    private val handler = GlobalExceptionHandler()

    @Test
    fun `should return 404 for CategoryNotFoundException`() {
        val result = handler.handleCategoryNotFound(CategoryNotFoundException(42L))
        assertThat(result.status).isEqualTo(HttpStatus.NOT_FOUND.value())
        assertThat(result.detail).contains("42")
    }

    @Test
    fun `should return 409 for CategoryAlreadyExistsException`() {
        val result = handler.handleCategoryAlreadyExists(CategoryAlreadyExistsException("Transport"))
        assertThat(result.status).isEqualTo(HttpStatus.CONFLICT.value())
        assertThat(result.detail).contains("Transport")
    }

    @Test
    fun `should return 404 for EntryNotFoundException`() {
        val result = handler.handleEntryNotFound(EntryNotFoundException(10L))
        assertThat(result.status).isEqualTo(HttpStatus.NOT_FOUND.value())
        assertThat(result.detail).contains("10")
    }

    @Test
    fun `should return 404 for SubcategoryNotFoundException`() {
        val result = handler.handleSubcategoryNotFound(SubcategoryNotFoundException(7L))
        assertThat(result.status).isEqualTo(HttpStatus.NOT_FOUND.value())
        assertThat(result.detail).contains("7")
    }

    @Test
    fun `should return 409 for SubcategoryAlreadyExistsException`() {
        val result = handler.handleSubcategoryAlreadyExists(SubcategoryAlreadyExistsException("Fuel", 3L))
        assertThat(result.status).isEqualTo(HttpStatus.CONFLICT.value())
        assertThat(result.detail).contains("Fuel")
    }

    @Test
    fun `should return 400 for InvalidPeriodException`() {
        val result = handler.handleInvalidPeriod(InvalidPeriodException("Start date must not be after end date"))
        assertThat(result.status).isEqualTo(HttpStatus.BAD_REQUEST.value())
        assertThat(result.detail).contains("Start date must not be after end date")
    }

    @Test
    fun `should return 422 for generic DomainException`() {
        val result = handler.handleDomain(CategoryNotFoundException(1L))
        assertThat(result.status).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value())
    }

    @Test
    fun `should return 500 for unexpected exception`() {
        val result = handler.handleUnexpected(RuntimeException("Unexpected"))
        assertThat(result.status).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value())
        assertThat(result.detail).doesNotContain("Unexpected")
    }

    @Test
    fun `should return 400 for HttpMessageNotReadableException`() {
        val ex = HttpMessageNotReadableException("JSON parse error", MockHttpInputMessage("{}" .toByteArray()))
        val result = handler.handleNotReadable(ex)
        assertThat(result.status).isEqualTo(HttpStatus.BAD_REQUEST.value())
        assertThat(result.detail).isEqualTo("Malformed or missing request body")
    }
    @Test
    fun `should return 400 for MissingServletRequestParameterException`() {
        val ex = MissingServletRequestParameterException("startDate", "LocalDate")
        val result = handler.handleMissingParam(ex)
        assertThat(result.status).isEqualTo(HttpStatus.BAD_REQUEST.value())
        assertThat(result.detail).contains("startDate")
    }}
