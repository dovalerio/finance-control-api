package com.heitor.finance.infrastructure.persistence.repository

import com.heitor.finance.infrastructure.persistence.entity.SubcategoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface SubcategoryJpaRepository : JpaRepository<SubcategoryEntity, Long> {
    fun findByCategoryId(categoryId: Long): List<SubcategoryEntity>
}
