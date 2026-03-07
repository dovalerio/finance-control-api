package com.heitor.finance.domain.model

data class Category(
    val id: Long? = null,
    val name: String,
    val subcategories: List<Subcategory> = emptyList()
) {
    init {
        require(name.isNotBlank()) { "Category name must not be blank" }
        val hasDuplicates = subcategories
            .groupingBy { it.name.lowercase() }
            .eachCount()
            .any { it.value > 1 }
        require(!hasDuplicates) { "Subcategory names must be unique within a category" }
    }
}
