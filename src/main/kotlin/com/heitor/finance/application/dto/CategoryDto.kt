package com.heitor.finance.application.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class CreateCategoryRequest(
    @JsonProperty("nome")
    @field:jakarta.validation.constraints.NotBlank(message = "O campo 'nome' é obrigatório")
    val name: String
)

data class UpdateCategoryRequest(
    @JsonProperty("nome")
    @field:jakarta.validation.constraints.NotBlank(message = "O campo 'nome' é obrigatório")
    val name: String
)

data class CategoryResponse(
    @JsonProperty("id_categoria") val id: Long,
    @JsonProperty("nome") val name: String
)
