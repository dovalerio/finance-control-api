package com.heitor.finance.application.usecase

import com.heitor.finance.application.dto.CategoryResponse
import com.heitor.finance.application.dto.CreateCategoryRequest
import com.heitor.finance.application.dto.toResponse
import com.heitor.finance.application.port.input.UpdateCategoryUseCase
import com.heitor.finance.application.port.output.CategoryOutputPort
import com.heitor.finance.domain.exception.CategoryAlreadyExistsException
import com.heitor.finance.domain.exception.CategoryNotFoundException

class UpdateCategoryUseCaseImpl(
    private val categoryOutputPort: CategoryOutputPort
) : UpdateCategoryUseCase {

    override fun execute(id: Long, request: CreateCategoryRequest): CategoryResponse {
        val existing = categoryOutputPort.findById(id) ?: throw CategoryNotFoundException(id)
        val name = request.name!!

        if (existing.name != name && categoryOutputPort.existsByName(name)) {
            throw CategoryAlreadyExistsException(name)
        }

        val updated = categoryOutputPort.update(existing.copy(name = name))
        return updated.toResponse()
    }
}
