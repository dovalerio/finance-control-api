package com.heitor.finance.application.port.input

import com.heitor.finance.application.dto.CategoryResponse

interface FindCategoryUseCase {
    fun findAll(): List<CategoryResponse>
    fun findById(id: Long): CategoryResponse
}
