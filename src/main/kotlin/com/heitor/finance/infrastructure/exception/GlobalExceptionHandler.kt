package com.heitor.finance.infrastructure.exception

import com.heitor.finance.domain.exception.CategoryAlreadyExistsException
import com.heitor.finance.domain.exception.CategoryNotFoundException
import com.heitor.finance.domain.exception.DomainException
import com.heitor.finance.domain.exception.InvalidPeriodException
import com.heitor.finance.domain.exception.SubcategoryAlreadyExistsException
import com.heitor.finance.domain.exception.SubcategoryNotFoundException
import org.apache.logging.log4j.LogManager
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LogManager.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(SubcategoryAlreadyExistsException::class)
    fun handleSubcategoryAlreadyExists(ex: SubcategoryAlreadyExistsException): ProblemDetail {
        logger.warn("Subcategory conflict: {}", ex.message)
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.message ?: "Subcategory already exists")
    }

    @ExceptionHandler(SubcategoryNotFoundException::class)
    fun handleSubcategoryNotFound(ex: SubcategoryNotFoundException): ProblemDetail {
        logger.warn("Subcategory not found: {}", ex.message)
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.message ?: "Not found")
    }

    @ExceptionHandler(CategoryAlreadyExistsException::class)
    fun handleCategoryAlreadyExists(ex: CategoryAlreadyExistsException): ProblemDetail {
        logger.warn("Category conflict: {}", ex.message)
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.message ?: "Category already exists")
    }

    @ExceptionHandler(CategoryNotFoundException::class)
    fun handleCategoryNotFound(ex: CategoryNotFoundException): ProblemDetail {
        logger.warn("Category not found: {}", ex.message)
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.message ?: "Not found")
    }

    @ExceptionHandler(InvalidPeriodException::class)
    fun handleInvalidPeriod(ex: InvalidPeriodException): ProblemDetail {
        logger.warn("Invalid period: {}", ex.message)
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.message ?: "Invalid period")
    }

    @ExceptionHandler(DomainException::class)
    fun handleDomain(ex: DomainException): ProblemDetail {
        logger.warn("Domain exception type={} message={}", ex::class.simpleName, ex.message)
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.message ?: "Domain error")
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ProblemDetail {
        val detail = ex.bindingResult.fieldErrors
            .joinToString("; ") { "${it.field}: ${it.defaultMessage}" }
        logger.warn("Validation error: {}", detail)
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail)
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingParam(ex: MissingServletRequestParameterException): ProblemDetail {
        logger.warn("Missing required parameter: {}", ex.parameterName)
        return ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "Required parameter '${ex.parameterName}' is missing"
        )
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleNotReadable(ex: HttpMessageNotReadableException): ProblemDetail {
        logger.warn("Malformed request body: {}", ex.message)
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Malformed or missing request body")
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(ex: Exception): ProblemDetail {
        logger.error("Unexpected error type={}", ex::class.simpleName, ex)
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred")
    }
}
