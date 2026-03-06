package com.heitor.finance.domain.model

data class Category(
    val id: Long? = null,
    val name: String,
    val subcategories: List<Subcategory> = emptyList()
)
