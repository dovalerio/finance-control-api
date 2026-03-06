package com.heitor.finance.application.usecase

import com.heitor.finance.application.dto.CreateCategoryRequest
import com.heitor.finance.application.dto.CategoryResponse
import com.heitor.finance.application.port.input.CreateCategoryUseCase
import com.heitor.finance.application.port.output.CategoryOutputPort
import com.heitor.finance.domain.exception.CategoryAlreadyExistsException
import com.heitor.finance.domain.model.Category

class CreateCategoryUseCaseImpl(
    private val categoryOutputPort: CategoryOutputPort
) : CreateCategoryUseCase {

    override fun execute(request: CreateCategoryRequest): CategoryResponse {
        if (categoryOutputPort.existsByName(request.name)) {
            throw CategoryAlreadyExistsException(request.name)
        }
        val saved = categoryOutputPort.save(Category(name = request.name))
        return CategoryResponse(id = saved.id!!, name = saved.name)
    }
}
