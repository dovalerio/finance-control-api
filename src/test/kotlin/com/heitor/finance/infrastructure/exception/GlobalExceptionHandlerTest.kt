package com.heitor.finance.infrastructure.exception

import com.heitor.finance.domain.exception.CategoryAlreadyExistsException
import com.heitor.finance.domain.exception.CategoryNotFoundException
import com.heitor.finance.domain.exception.EntryNotFoundException
import com.heitor.finance.domain.exception.InvalidEntryAmountException
import com.heitor.finance.domain.exception.InvalidPeriodException
import com.heitor.finance.domain.exception.SubcategoryAlreadyExistsException
import com.heitor.finance.domain.exception.SubcategoryHasEntriesException
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
        assertThat(result.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        assertThat(result.body!!.mensagem).contains("42")
    }

    @Test
    fun `should return 409 for CategoryAlreadyExistsException`() {
        val result = handler.handleCategoryAlreadyExists(CategoryAlreadyExistsException("Transport"))
        assertThat(result.statusCode).isEqualTo(HttpStatus.CONFLICT)
        assertThat(result.body!!.mensagem).contains("Transport")
    }

    @Test
    fun `should return 404 for EntryNotFoundException`() {
        val result = handler.handleEntryNotFound(EntryNotFoundException(10L))
        assertThat(result.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        assertThat(result.body!!.mensagem).contains("10")
    }

    @Test
    fun `should return 404 for SubcategoryNotFoundException`() {
        val result = handler.handleSubcategoryNotFound(SubcategoryNotFoundException(7L))
        assertThat(result.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        assertThat(result.body!!.mensagem).contains("7")
    }

    @Test
    fun `should return 409 for SubcategoryAlreadyExistsException`() {
        val result = handler.handleSubcategoryAlreadyExists(SubcategoryAlreadyExistsException("Fuel", 3L))
        assertThat(result.statusCode).isEqualTo(HttpStatus.CONFLICT)
        assertThat(result.body!!.mensagem).contains("Fuel")
    }

    @Test
    fun `should return 400 for InvalidPeriodException`() {
        val result = handler.handleInvalidPeriod(InvalidPeriodException("Start date must not be after end date"))
        assertThat(result.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(result.body!!.mensagem).contains("Start date must not be after end date")
    }

    @Test
    fun `should return 422 for generic DomainException`() {
        val result = handler.handleDomain(CategoryNotFoundException(1L))
        assertThat(result.statusCode).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
    }

    @Test
    fun `should return 500 for unexpected exception`() {
        val result = handler.handleUnexpected(RuntimeException("Unexpected"))
        assertThat(result.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
        assertThat(result.body!!.mensagem).doesNotContain("Unexpected")
    }

    @Test
    fun `should return 400 for HttpMessageNotReadableException`() {
        val ex = HttpMessageNotReadableException("JSON parse error", MockHttpInputMessage("{}".toByteArray()))
        val result = handler.handleNotReadable(ex)
        assertThat(result.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(result.body!!.mensagem).isEqualTo("Malformed or missing request body")
    }

    @Test
    fun `should return 400 for MissingServletRequestParameterException`() {
        val ex = MissingServletRequestParameterException("data_inicio", "LocalDate")
        val result = handler.handleMissingParam(ex)
        assertThat(result.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(result.body!!.mensagem).contains("data_inicio")
    }

    @Test
    fun `should return 422 for InvalidEntryAmountException`() {
        val result = handler.handleInvalidEntryAmount(InvalidEntryAmountException())
        assertThat(result.statusCode).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
        assertThat(result.body!!.codigo).isEqualTo("valor_invalido")
    }

    @Test
    fun `should return 409 for SubcategoryHasEntriesException`() {
        val result = handler.handleSubcategoryHasEntries(SubcategoryHasEntriesException(5L))
        assertThat(result.statusCode).isEqualTo(HttpStatus.CONFLICT)
        assertThat(result.body!!.codigo).isEqualTo("conflito")
        assertThat(result.body!!.mensagem).contains("5")
    }
}
