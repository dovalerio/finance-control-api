package com.heitor.finance.infrastructure.persistence.mapper

import com.heitor.finance.domain.model.Entry
import com.heitor.finance.domain.model.EntryType
import com.heitor.finance.domain.valueobject.Money
import com.heitor.finance.infrastructure.persistence.entity.CategoryEntity
import com.heitor.finance.infrastructure.persistence.entity.EntryEntity
import com.heitor.finance.infrastructure.persistence.entity.SubcategoryEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class EntryMapperTest {

    private val categoryEntity = CategoryEntity(id = 1L, name = "Food")
    private val subcategoryEntity = SubcategoryEntity(id = 10L, name = "Snacks", category = categoryEntity)
    private val date = LocalDate.of(2024, 1, 15)

    @Test
    fun `toDomain should map EntryEntity to Entry with subcategory`() {
        val entity = EntryEntity(
            id = 100L,
            comment ="Groceries",
            amount = BigDecimal("49.90"),
            type = EntryType.EXPENSE,
            date = date,
            category = categoryEntity,
            subcategory = subcategoryEntity
        )

        val entry = EntryMapper.toDomain(entity)

        assertEquals(100L, entry.id)
        assertEquals("Groceries", entry.comment)
        assertEquals(BigDecimal("49.90"), entry.amount.amount)
        assertEquals(EntryType.EXPENSE, entry.type)
        assertEquals(date, entry.date)
        assertEquals(1L, entry.categoryId)
        assertEquals(10L, entry.subcategoryId)
    }

    @Test
    fun `toDomain should map EntryEntity to Entry without subcategory`() {
        val entity = EntryEntity(
            id = 101L,
            comment ="Salary",
            amount = BigDecimal("5000.00"),
            type = EntryType.INCOME,
            date = date,
            category = categoryEntity,
            subcategory = null
        )

        val entry = EntryMapper.toDomain(entity)

        assertEquals(101L, entry.id)
        assertEquals("Salary", entry.comment)
        assertEquals(BigDecimal("5000.00"), entry.amount.amount)
        assertEquals(EntryType.INCOME, entry.type)
        assertEquals(1L, entry.categoryId)
        assertNull(entry.subcategoryId)
    }

    @Test
    fun `toEntity should map Entry to EntryEntity with subcategory`() {
        val entry = Entry(
            id = 100L,
            comment ="Groceries",
            amount = Money.of(BigDecimal("49.90")),
            type = EntryType.EXPENSE,
            date = date,
            categoryId = 1L,
            subcategoryId = 10L
        )

        val entity = EntryMapper.toEntity(entry, categoryEntity, subcategoryEntity)

        assertEquals(100L, entity.id)
        assertEquals("Groceries", entity.comment)
        assertEquals(BigDecimal("49.90"), entity.amount)
        assertEquals(EntryType.EXPENSE, entity.type)
        assertEquals(date, entity.date)
        assertEquals(categoryEntity, entity.category)
        assertEquals(subcategoryEntity, entity.subcategory)
    }

    @Test
    fun `toEntity should map Entry to EntryEntity without subcategory`() {
        val entry = Entry(
            id = null,
            comment ="Rent",
            amount = Money.of(BigDecimal("1200.00")),
            type = EntryType.EXPENSE,
            date = date,
            categoryId = 1L,
            subcategoryId = null
        )

        val entity = EntryMapper.toEntity(entry, categoryEntity, null)

        assertNull(entity.id)
        assertEquals("Rent", entity.comment)
        assertEquals(BigDecimal("1200.00"), entity.amount)
        assertNull(entity.subcategory)
    }
}
