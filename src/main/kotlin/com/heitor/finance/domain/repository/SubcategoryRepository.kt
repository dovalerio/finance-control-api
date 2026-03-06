package com.heitor.finance.domain.repository

import com.heitor.finance.domain.model.Subcategory

interface SubcategoryRepository {
    fun findAll(): List<Subcategory>
    fun findById(id: Long): Subcategory?
    fun findByCategoryId(categoryId: Long): List<Subcategory>
    fun save(subcategory: Subcategory): Subcategory
    fun deleteById(id: Long)
}
