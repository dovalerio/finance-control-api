package com.heitor.finance.infrastructure.persistence.adapter

import com.heitor.finance.domain.exception.CategoryNotFoundException
import com.heitor.finance.domain.exception.SubcategoryNotFoundException
import com.heitor.finance.domain.model.Entry
import com.heitor.finance.domain.model.EntryType
import com.heitor.finance.domain.valueobject.Money
import com.heitor.finance.domain.valueobject.Period
import com.heitor.finance.infrastructure.persistence.entity.CategoryEntity
import com.heitor.finance.infrastructure.persistence.entity.EntryEntity
import com.heitor.finance.infrastructure.persistence.entity.SubcategoryEntity
import com.heitor.finance.infrastructure.persistence.repository.CategoryJpaRepository
import com.heitor.finance.infrastructure.persistence.repository.EntryJpaRepository
import com.heitor.finance.infrastructure.persistence.repository.SubcategoryJpaRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.LocalDate
import java.util.Optional

class EntryRepositoryAdapterTest {

    private val entryJpaRepository = mockk<EntryJpaRepository>()
    private val categoryJpaRepository = mockk<CategoryJpaRepository>()
    private val subcategoryJpaRepository = mockk<SubcategoryJpaRepository>()
    private val adapter = EntryRepositoryAdapter(entryJpaRepository, categoryJpaRepository, subcategoryJpaRepository)

    private val categoryEntity = CategoryEntity(id = 1L, name = "Food")
    private val subcategoryEntity = SubcategoryEntity(id = 10L, name = "Snacks", category = categoryEntity)
    private val date = LocalDate.of(2024, 1, 15)
    private val entryEntity = EntryEntity(
        id = 100L,
        description = "Groceries",
        amount = BigDecimal("49.90"),
        type = EntryType.EXPENSE,
        date = date,
        category = categoryEntity,
        subcategory = subcategoryEntity
    )

    @Test
    fun `findAll should return all entries`() {
        every { entryJpaRepository.findAll() } returns listOf(entryEntity)

        val result = adapter.findAll()

        assertEquals(1, result.size)
        assertEquals(100L, result[0].id)
        assertEquals("Groceries", result[0].description)
    }

    @Test
    fun `findAll should return empty list when no entries`() {
        every { entryJpaRepository.findAll() } returns emptyList()

        val result = adapter.findAll()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `findById should return entry when found`() {
        every { entryJpaRepository.findById(100L) } returns Optional.of(entryEntity)

        val result = adapter.findById(100L)

        assertEquals(100L, result?.id)
        assertEquals("Groceries", result?.description)
    }

    @Test
    fun `findById should return null when not found`() {
        every { entryJpaRepository.findById(999L) } returns Optional.empty()

        val result = adapter.findById(999L)

        assertNull(result)
    }

    @Test
    fun `findByPeriod should return entries within period`() {
        val period = Period(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31))
        every {
            entryJpaRepository.findByPeriod(period.startDate, period.endDate)
        } returns listOf(entryEntity)

        val result = adapter.findByPeriod(period)

        assertEquals(1, result.size)
        assertEquals(100L, result[0].id)
    }

    @Test
    fun `findByPeriod should return empty list when no entries in period`() {
        val period = Period(LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 31))
        every {
            entryJpaRepository.findByPeriod(period.startDate, period.endDate)
        } returns emptyList()

        val result = adapter.findByPeriod(period)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `findByPeriodAndCategoryId should return entries filtered by category`() {
        val period = Period(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31))
        every {
            entryJpaRepository.findByPeriodAndCategoryId(period.startDate, period.endDate, 1L)
        } returns listOf(entryEntity)

        val result = adapter.findByPeriodAndCategoryId(period, 1L)

        assertEquals(1, result.size)
        assertEquals(1L, result[0].categoryId)
    }

    @Test
    fun `save should persist entry with subcategory and return domain model`() {
        val entry = Entry(
            id = null,
            description = "Groceries",
            amount = Money.of(BigDecimal("49.90")),
            type = EntryType.EXPENSE,
            date = date,
            categoryId = 1L,
            subcategoryId = 10L
        )
        val savedEntry = EntryEntity(
            id = 100L, description = "Groceries", amount = BigDecimal("49.90"),
            type = EntryType.EXPENSE, date = date, category = categoryEntity, subcategory = subcategoryEntity
        )
        every { categoryJpaRepository.findById(1L) } returns Optional.of(categoryEntity)
        every { subcategoryJpaRepository.findById(10L) } returns Optional.of(subcategoryEntity)
        every { entryJpaRepository.save(any()) } returns savedEntry

        val result = adapter.save(entry)

        assertEquals("Groceries", result.description)
        assertEquals(EntryType.EXPENSE, result.type)
        verify { entryJpaRepository.save(any()) }
    }

    @Test
    fun `save should persist entry without subcategory`() {
        val entry = Entry(
            id = null,
            description = "Salary",
            amount = Money.of(BigDecimal("5000.00")),
            type = EntryType.INCOME,
            date = date,
            categoryId = 1L,
            subcategoryId = null
        )
        val savedEntity = EntryEntity(
            id = 101L, description = "Salary", amount = BigDecimal("5000.00"),
            type = EntryType.INCOME, date = date, category = categoryEntity
        )
        every { categoryJpaRepository.findById(1L) } returns Optional.of(categoryEntity)
        every { entryJpaRepository.save(any()) } returns savedEntity

        val result = adapter.save(entry)

        assertEquals(101L, result.id)
        assertEquals("Salary", result.description)
        assertNull(result.subcategoryId)
    }

    @Test
    fun `save should throw CategoryNotFoundException when category not found`() {
        val entry = Entry(
            id = null, description = "Test", amount = Money.of(BigDecimal("10.00")),
            type = EntryType.EXPENSE, date = date, categoryId = 99L, subcategoryId = null
        )
        every { categoryJpaRepository.findById(99L) } returns Optional.empty()

        assertThrows<CategoryNotFoundException> {
            adapter.save(entry)
        }
    }

    @Test
    fun `save should throw SubcategoryNotFoundException when subcategory not found`() {
        val entry = Entry(
            id = null, description = "Test", amount = Money.of(BigDecimal("10.00")),
            type = EntryType.EXPENSE, date = date, categoryId = 1L, subcategoryId = 99L
        )
        every { categoryJpaRepository.findById(1L) } returns Optional.of(categoryEntity)
        every { subcategoryJpaRepository.findById(99L) } returns Optional.empty()

        assertThrows<SubcategoryNotFoundException> {
            adapter.save(entry)
        }
    }

    @Test
    fun `deleteById should delegate to JPA repository`() {
        every { entryJpaRepository.deleteById(100L) } returns Unit

        adapter.deleteById(100L)

        verify { entryJpaRepository.deleteById(100L) }
    }
}
