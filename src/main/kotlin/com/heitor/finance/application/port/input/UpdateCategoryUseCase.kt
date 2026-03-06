package com.heitor.finance.application.port.input

import com.heitor.finance.application.dto.CategoryResponse
import com.heitor.finance.application.dto.CreateCategoryRequest

interface UpdateCategoryUseCase {
    fun execute(id: Long, request: CreateCategoryRequest): CategoryResponse
}
