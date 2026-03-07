package com.heitor.finance.domain.model

data class Subcategory(
    val id: Long? = null,
    val name: String,
    val categoryId: Long
) {
    init {
        require(name.isNotBlank()) { "Subcategory name must not be blank" }
    }
}
