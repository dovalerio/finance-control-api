package com.heitor.finance.application.dto

data class CreateCategoryRequest(
    @field:jakarta.validation.constraints.NotBlank(message = "name must not be blank")
    val name: String
)

data class UpdateCategoryRequest(
    @field:jakarta.validation.constraints.NotBlank(message = "name must not be blank")
    val name: String
)

data class CategoryResponse(
    val id: Long,
    val name: String
)
