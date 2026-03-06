package com.heitor.finance.infrastructure.persistence.repository

import com.heitor.finance.infrastructure.persistence.entity.CategoryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional

interface CategoryJpaRepository : JpaRepository<CategoryEntity, Long> {

    @Query("SELECT c FROM CategoryEntity c LEFT JOIN FETCH c.subcategories WHERE (:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    fun findAllWithSubcategories(@Param("name") name: String?): List<CategoryEntity>

    @Query("SELECT c FROM CategoryEntity c LEFT JOIN FETCH c.subcategories WHERE c.id = :id")
    fun findByIdWithSubcategories(@Param("id") id: Long): Optional<CategoryEntity>

    fun existsByName(name: String): Boolean
}
