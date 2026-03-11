package com.heitor.finance.application.port.input

import com.heitor.finance.application.dto.CategoryRequest
import com.heitor.finance.application.dto.CategoryResponse

interface CreateCategoryUseCase {
    fun execute(request: CategoryRequest): CategoryResponse
}
