package com.heitor.finance.infrastructure.persistence.repository

import com.heitor.finance.infrastructure.persistence.entity.SubcategoryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface SubcategoryJpaRepository : JpaRepository<SubcategoryEntity, Long> {

    fun findByCategoryId(categoryId: Long): List<SubcategoryEntity>

    fun existsByNameAndCategoryId(name: String, categoryId: Long): Boolean

    fun findByNameContainingIgnoreCase(name: String): List<SubcategoryEntity>

    fun findByNameContainingIgnoreCaseAndCategoryId(name: String, categoryId: Long): List<SubcategoryEntity>
}

