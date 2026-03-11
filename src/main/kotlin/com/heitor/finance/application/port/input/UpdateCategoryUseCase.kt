package com.heitor.finance.application.port.input

import com.heitor.finance.application.dto.CategoryRequest
import com.heitor.finance.application.dto.CategoryResponse

interface UpdateCategoryUseCase {
    fun execute(id: Long, request: CategoryRequest): CategoryResponse
}
