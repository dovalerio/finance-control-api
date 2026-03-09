package com.heitor.finance.application.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class CreateSubcategoryRequest(
    @JsonProperty("nome")
    @field:jakarta.validation.constraints.NotBlank(message = "O campo 'nome' é obrigatório")
    val name: String,
    @JsonProperty("id_categoria")
    @field:jakarta.validation.constraints.NotNull(message = "O campo 'id_categoria' é obrigatório")
    val categoryId: Long
)

data class SubcategoryResponse(
    @JsonProperty("id_subcategoria") val id: Long,
    @JsonProperty("nome") val name: String,
    @JsonProperty("id_categoria") val categoryId: Long
)
