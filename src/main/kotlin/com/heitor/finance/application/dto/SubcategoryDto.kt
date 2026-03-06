package com.heitor.finance.application.dto

data class CreateSubcategoryRequest(
    val name: String,
    val categoryId: Long
)

data class SubcategoryResponse(
    val id: Long,
    val name: String,
    val categoryId: Long
)
