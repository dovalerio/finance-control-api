package com.heitor.finance.infrastructure.persistence.repository

import com.heitor.finance.infrastructure.persistence.entity.SubcategoryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface SubcategoryJpaRepository : JpaRepository<SubcategoryEntity, Long> {

    fun findByCategoryId(categoryId: Long): List<SubcategoryEntity>

    fun existsByNameAndCategoryId(name: String, categoryId: Long): Boolean

    @Query(
        """
        SELECT s FROM SubcategoryEntity s
        WHERE (:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%')))
        AND (:categoryId IS NULL OR s.category.id = :categoryId)
        """
    )
    fun findByFilters(name: String?, categoryId: Long?): List<SubcategoryEntity>
}

