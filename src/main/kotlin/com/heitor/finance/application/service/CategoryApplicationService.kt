package com.heitor.finance.application.service

import com.heitor.finance.application.dto.CategoryResponse
import com.heitor.finance.application.port.input.FindCategoryUseCase
import com.heitor.finance.application.port.output.CategoryOutputPort
import com.heitor.finance.domain.exception.CategoryNotFoundException

class CategoryApplicationService(
    private val categoryOutputPort: CategoryOutputPort
) : FindCategoryUseCase {

    override fun findAll(): List<CategoryResponse> =
        categoryOutputPort.findAll().map { CategoryResponse(id = it.id!!, name = it.name) }

    override fun findById(id: Long): CategoryResponse {
        val category = categoryOutputPort.findById(id) ?: throw CategoryNotFoundException(id)
        return CategoryResponse(id = category.id!!, name = category.name)
    }
}
