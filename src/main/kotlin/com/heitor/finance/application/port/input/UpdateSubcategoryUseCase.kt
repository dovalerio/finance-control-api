package com.heitor.finance.application.port.input

import com.heitor.finance.application.dto.CreateSubcategoryRequest
import com.heitor.finance.application.dto.SubcategoryResponse

interface UpdateSubcategoryUseCase {
    fun execute(id: Long, request: CreateSubcategoryRequest): SubcategoryResponse
}
