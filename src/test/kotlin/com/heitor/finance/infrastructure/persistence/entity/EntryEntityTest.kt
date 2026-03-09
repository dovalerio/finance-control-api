package com.heitor.finance.infrastructure.persistence.entity

import com.heitor.finance.domain.model.EntryType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class EntryEntityTest {

    private val categoryEntity = CategoryEntity(id = 1L, name = "Food")
    private val subcategoryEntity = SubcategoryEntity(id = 10L, name = "Snacks", category = categoryEntity)
    private val date = LocalDate.of(2024, 1, 15)

    @Test
    fun `should create EntryEntity with all fields including subcategory`() {
        val entity = EntryEntity(
            id = 100L,
            comment ="Groceries",
            amount = BigDecimal("49.90"),
            type = EntryType.EXPENSE,
            date = date,
            category = categoryEntity,
            subcategory = subcategoryEntity
        )

        assertEquals(100L, entity.id)
        assertEquals("Groceries", entity.comment)
        assertEquals(BigDecimal("49.90"), entity.amount)
        assertEquals(EntryType.EXPENSE, entity.type)
        assertEquals(date, entity.date)
        assertEquals(categoryEntity, entity.category)
        assertEquals(subcategoryEntity, entity.subcategory)
    }

    @Test
    fun `should create EntryEntity without subcategory`() {
        val entity = EntryEntity(
            id = 101L,
            comment ="Salary",
            amount = BigDecimal("5000.00"),
            type = EntryType.INCOME,
            date = date,
            category = categoryEntity
        )

        assertEquals(101L, entity.id)
        assertEquals("Salary", entity.comment)
        assertEquals(EntryType.INCOME, entity.type)
        assertNull(entity.subcategory)
    }

    @Test
    fun `should create EntryEntity with null id`() {
        val entity = EntryEntity(
            comment ="Rent",
            amount = BigDecimal("1200.00"),
            type = EntryType.EXPENSE,
            date = date,
            category = categoryEntity
        )

        assertNull(entity.id)
        assertEquals("Rent", entity.comment)
    }

    @Test
    fun `should create EntryEntity with INCOME type`() {
        val entity = EntryEntity(
            id = 200L,
            comment ="Freelance",
            amount = BigDecimal("2500.00"),
            type = EntryType.INCOME,
            date = LocalDate.of(2024, 3, 1),
            category = categoryEntity
        )

        assertEquals(EntryType.INCOME, entity.type)
        assertEquals(BigDecimal("2500.00"), entity.amount)
    }
}
