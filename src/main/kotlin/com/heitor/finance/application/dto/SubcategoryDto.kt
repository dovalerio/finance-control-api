package com.heitor.finance.application.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class CreateSubcategoryRequest(
    @JsonProperty("nome") val name: String,
    @JsonProperty("id_categoria") val categoryId: Long
)

data class SubcategoryResponse(
    @JsonProperty("id_subcategoria") val id: Long,
    @JsonProperty("nome") val name: String,
    @JsonProperty("id_categoria") val categoryId: Long
)
