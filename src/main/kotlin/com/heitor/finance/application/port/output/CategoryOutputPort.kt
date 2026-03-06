package com.heitor.finance.application.port.output

import com.heitor.finance.domain.model.Category

interface CategoryOutputPort {
    fun findAll(name: String? = null): List<Category>
    fun findById(id: Long): Category?
    fun save(category: Category): Category
    fun update(category: Category): Category
    fun deleteById(id: Long)
    fun existsById(id: Long): Boolean
    fun existsByName(name: String): Boolean
}
