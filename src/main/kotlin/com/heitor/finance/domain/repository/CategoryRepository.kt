package com.heitor.finance.domain.repository

import com.heitor.finance.domain.model.Category

interface CategoryRepository {
    fun findAll(): List<Category>
    fun findById(id: Long): Category?
    fun save(category: Category): Category
    fun update(category: Category): Category
    fun deleteById(id: Long)
    fun existsById(id: Long): Boolean
}
