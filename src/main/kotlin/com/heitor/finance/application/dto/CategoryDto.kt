package com.heitor.finance.application.dto

data class CreateCategoryRequest(
    val name: String
)

data class UpdateCategoryRequest(
    val name: String
)

data class CategoryResponse(
    val id: Long,
    val name: String
)
