package com.heitor.finance.application.usecase

import com.heitor.finance.application.dto.CategoryResponse
import com.heitor.finance.application.dto.toResponse
import com.heitor.finance.application.port.input.FindCategoryUseCase
import com.heitor.finance.application.port.output.CategoryOutputPort
import com.heitor.finance.domain.exception.CategoryNotFoundException

class FindCategoryUseCaseImpl(
    private val categoryOutputPort: CategoryOutputPort
) : FindCategoryUseCase {

    override fun findAll(name: String?): List<CategoryResponse> =
        categoryOutputPort.findAll(name).map { it.toResponse() }

    override fun findById(id: Long): CategoryResponse {
        val category = categoryOutputPort.findById(id) ?: throw CategoryNotFoundException(id)
        return category.toResponse()
    }
}
