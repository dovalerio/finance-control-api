package com.heitor.finance.application.usecase

import com.heitor.finance.application.dto.CreateCategoryRequest
import com.heitor.finance.application.dto.CategoryResponse
import com.heitor.finance.application.port.input.CreateCategoryUseCase
import com.heitor.finance.application.port.output.CategoryOutputPort
import com.heitor.finance.domain.model.Category

class CreateCategoryUseCaseImpl(
    private val categoryOutputPort: CategoryOutputPort
) : CreateCategoryUseCase {

    override fun execute(request: CreateCategoryRequest): CategoryResponse {
        val category = Category(name = request.name)
        val saved = categoryOutputPort.save(category)
        return CategoryResponse(id = saved.id!!, name = saved.name)
    }
}
