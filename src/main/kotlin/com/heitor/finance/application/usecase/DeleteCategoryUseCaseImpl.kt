package com.heitor.finance.application.usecase

import com.heitor.finance.application.port.input.DeleteCategoryUseCase
import com.heitor.finance.application.port.output.CategoryOutputPort
import com.heitor.finance.domain.exception.CategoryNotFoundException

class DeleteCategoryUseCaseImpl(
    private val categoryOutputPort: CategoryOutputPort
) : DeleteCategoryUseCase {

    override fun execute(id: Long) {
        if (!categoryOutputPort.existsById(id)) throw CategoryNotFoundException(id)
        categoryOutputPort.deleteById(id)
    }
}
