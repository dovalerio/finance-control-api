package com.heitor.finance.infrastructure.exception

import com.heitor.finance.domain.exception.CategoryAlreadyExistsException
import com.heitor.finance.domain.exception.CategoryNotFoundException
import com.heitor.finance.domain.exception.DomainException
import com.heitor.finance.domain.exception.EntryNotFoundException
import com.heitor.finance.domain.exception.InvalidEntryAmountException
import com.heitor.finance.domain.exception.InvalidPeriodException
import com.heitor.finance.domain.exception.SubcategoryAlreadyExistsException
import com.heitor.finance.domain.exception.SubcategoryHasEntriesException
import com.heitor.finance.domain.exception.SubcategoryNotFoundException
import org.apache.logging.log4j.LogManager
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

data class ErrorResponse(val codigo: String, val mensagem: String)

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LogManager.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(InvalidEntryAmountException::class)
    fun handleInvalidEntryAmount(ex: InvalidEntryAmountException): ResponseEntity<ErrorResponse> {
        logger.warn("Invalid entry amount: {}", ex.message)
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
            ErrorResponse(codigo = "valor_invalido", mensagem = ex.message ?: "O valor do lançamento não pode ser zero")
        )
    }

    @ExceptionHandler(SubcategoryHasEntriesException::class)
    fun handleSubcategoryHasEntries(ex: SubcategoryHasEntriesException): ResponseEntity<ErrorResponse> {
        logger.warn("Subcategory delete blocked: {}", ex.message)
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
            ErrorResponse(codigo = "conflito", mensagem = ex.message ?: "Subcategoria possui lançamentos e não pode ser excluída")
        )
    }

    @ExceptionHandler(SubcategoryAlreadyExistsException::class)
    fun handleSubcategoryAlreadyExists(ex: SubcategoryAlreadyExistsException): ResponseEntity<ErrorResponse> {
        logger.warn("Subcategory conflict: {}", ex.message)
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
            ErrorResponse(codigo = "conflito", mensagem = ex.message ?: "Subcategoria já existe")
        )
    }

    @ExceptionHandler(SubcategoryNotFoundException::class)
    fun handleSubcategoryNotFound(ex: SubcategoryNotFoundException): ResponseEntity<ErrorResponse> {
        logger.warn("Subcategory not found: {}", ex.message)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ErrorResponse(codigo = "nao_encontrado", mensagem = ex.message ?: "Not found")
        )
    }

    @ExceptionHandler(EntryNotFoundException::class)
    fun handleEntryNotFound(ex: EntryNotFoundException): ResponseEntity<ErrorResponse> {
        logger.warn("Entry not found: {}", ex.message)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ErrorResponse(codigo = "nao_encontrado", mensagem = ex.message ?: "Not found")
        )
    }

    @ExceptionHandler(CategoryAlreadyExistsException::class)
    fun handleCategoryAlreadyExists(ex: CategoryAlreadyExistsException): ResponseEntity<ErrorResponse> {
        logger.warn("Category conflict: {}", ex.message)
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
            ErrorResponse(codigo = "conflito", mensagem = ex.message ?: "Categoria já existe")
        )
    }

    @ExceptionHandler(CategoryNotFoundException::class)
    fun handleCategoryNotFound(ex: CategoryNotFoundException): ResponseEntity<ErrorResponse> {
        logger.warn("Category not found: {}", ex.message)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ErrorResponse(codigo = "nao_encontrado", mensagem = ex.message ?: "Not found")
        )
    }

    @ExceptionHandler(InvalidPeriodException::class)
    fun handleInvalidPeriod(ex: InvalidPeriodException): ResponseEntity<ErrorResponse> {
        logger.warn("Invalid period: {}", ex.message)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse(codigo = "periodo_invalido", mensagem = ex.message ?: "Período inválido")
        )
    }

    @ExceptionHandler(DomainException::class)
    fun handleDomain(ex: DomainException): ResponseEntity<ErrorResponse> {
        logger.warn("Domain exception type={} message={}", ex::class.simpleName, ex.message)
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
            ErrorResponse(codigo = "erro_dominio", mensagem = ex.message ?: "Erro de domínio")
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val detail = ex.bindingResult.fieldErrors
            .joinToString("; ") { it.defaultMessage ?: "Campo inválido" }
        logger.warn("Validation error: {}", detail)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse(codigo = "erro_validacao", mensagem = detail)
        )
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingParam(ex: MissingServletRequestParameterException): ResponseEntity<ErrorResponse> {
        logger.warn("Missing required parameter: {}", ex.parameterName)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse(
                codigo = "parametro_ausente",
                mensagem = "Required parameter '${ex.parameterName}' is missing"
            )
        )
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(ex: MethodArgumentTypeMismatchException): ResponseEntity<ErrorResponse> {
        logger.warn("Type mismatch for parameter: {}", ex.name)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse(codigo = "parametro_invalido", mensagem = "Parâmetro '${ex.name}' com formato inválido")
        )
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleNotReadable(ex: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
        logger.warn("Malformed request body: {}", ex.message)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse(codigo = "corpo_invalido", mensagem = "Malformed or missing request body")
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(ex: Exception): ResponseEntity<ErrorResponse> {
        logger.error("Unexpected error type={}", ex::class.simpleName, ex)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ErrorResponse(codigo = "erro_interno", mensagem = "Erro interno do servidor")
        )
    }
}
