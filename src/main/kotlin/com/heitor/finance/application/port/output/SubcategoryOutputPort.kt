package com.heitor.finance.application.port.output

import com.heitor.finance.domain.model.Subcategory

interface SubcategoryOutputPort {
    fun findAll(name: String? = null, categoryId: Long? = null): List<Subcategory>
    fun findById(id: Long): Subcategory?
    fun save(subcategory: Subcategory): Subcategory
    fun update(subcategory: Subcategory): Subcategory
    fun deleteById(id: Long)
    fun existsByNameInCategory(name: String, categoryId: Long): Boolean
}
