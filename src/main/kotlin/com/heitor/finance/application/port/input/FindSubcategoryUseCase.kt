package com.heitor.finance.application.port.input

import com.heitor.finance.application.dto.SubcategoryResponse

interface FindSubcategoryUseCase {
    fun findAll(name: String? = null, categoryId: Long? = null): List<SubcategoryResponse>
    fun findById(id: Long): SubcategoryResponse
}
