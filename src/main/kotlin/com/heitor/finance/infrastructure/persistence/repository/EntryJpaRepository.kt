package com.heitor.finance.infrastructure.persistence.repository

import com.heitor.finance.infrastructure.persistence.entity.EntryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface EntryJpaRepository : JpaRepository<EntryEntity, Long> {

    @Query(
        """
        SELECT e FROM EntryEntity e
        WHERE e.date BETWEEN :startDate AND :endDate
        """
    )
    fun findByPeriod(
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): List<EntryEntity>

    @Query(
        """
        SELECT e FROM EntryEntity e
        WHERE e.date BETWEEN :startDate AND :endDate
        AND e.category.id = :categoryId
        """
    )
    fun findByPeriodAndCategoryId(
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate,
        @Param("categoryId") categoryId: Long
    ): List<EntryEntity>

    fun findBySubcategoryId(subcategoryId: Long): List<EntryEntity>

    fun existsBySubcategoryId(subcategoryId: Long): Boolean

    @Query(
        """
        SELECT e FROM EntryEntity e
        WHERE e.subcategory.id = :subcategoryId
        AND e.date BETWEEN :startDate AND :endDate
        """
    )
    fun findBySubcategoryIdAndPeriod(
        @Param("subcategoryId") subcategoryId: Long,
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): List<EntryEntity>
}
