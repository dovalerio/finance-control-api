package com.heitor.finance.infrastructure.exception

import com.heitor.finance.domain.exception.CategoryNotFoundException
import com.heitor.finance.domain.exception.DomainException
import com.heitor.finance.domain.exception.InvalidPeriodException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(CategoryNotFoundException::class)
    fun handleCategoryNotFound(ex: CategoryNotFoundException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.message ?: "Not found")

    @ExceptionHandler(InvalidPeriodException::class)
    fun handleInvalidPeriod(ex: InvalidPeriodException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.message ?: "Invalid period")

    @ExceptionHandler(DomainException::class)
    fun handleDomain(ex: DomainException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.message ?: "Domain error")

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ProblemDetail {
        val detail = ex.bindingResult.fieldErrors
            .joinToString("; ") { "${it.field}: ${it.defaultMessage}" }
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail)
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(ex: Exception): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred")
}
