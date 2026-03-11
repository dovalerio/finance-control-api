package com.heitor.finance.application.port.input

import com.heitor.finance.application.dto.SubcategoryResponse

interface FindSubcategoryUseCase {
    fun findAll(name: String? = null, subcategoryId: Long? = null): List<SubcategoryResponse>
    fun findById(id: Long): SubcategoryResponse
}
