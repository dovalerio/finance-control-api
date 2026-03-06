package com.heitor.finance.application.port.input

import com.heitor.finance.application.dto.CreateCategoryRequest
import com.heitor.finance.application.dto.CategoryResponse

interface CreateCategoryUseCase {
    fun execute(request: CreateCategoryRequest): CategoryResponse
}
