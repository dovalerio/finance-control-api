package com.heitor.finance.infrastructure.persistence.adapter

import com.heitor.finance.domain.exception.CategoryNotFoundException
import com.heitor.finance.domain.model.Subcategory
import com.heitor.finance.infrastructure.persistence.entity.CategoryEntity
import com.heitor.finance.infrastructure.persistence.entity.SubcategoryEntity
import com.heitor.finance.infrastructure.persistence.repository.CategoryJpaRepository
import com.heitor.finance.infrastructure.persistence.repository.SubcategoryJpaRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.Optional

class SubcategoryRepositoryAdapterTest {

    private val subcategoryJpaRepository = mockk<SubcategoryJpaRepository>()
    private val categoryJpaRepository = mockk<CategoryJpaRepository>()
    private val adapter = SubcategoryRepositoryAdapter(subcategoryJpaRepository, categoryJpaRepository)

    private val categoryEntity = CategoryEntity(id = 1L, name = "Food")
    private val subcategoryEntity = SubcategoryEntity(id = 10L, name = "Snacks", category = categoryEntity)

    @Test
    fun `findAll should return all subcategories without filters`() {
        every { subcategoryJpaRepository.findByFilters(null, null) } returns listOf(subcategoryEntity)

        val result = adapter.findAll(null, null)

        assertEquals(1, result.size)
        assertEquals(10L, result[0].id)
        assertEquals("Snacks", result[0].name)
    }

    @Test
    fun `findAll should filter by name`() {
        every { subcategoryJpaRepository.findByFilters("Snacks", null) } returns listOf(subcategoryEntity)

        val result = adapter.findAll("Snacks", null)

        assertEquals(1, result.size)
        assertEquals("Snacks", result[0].name)
    }

    @Test
    fun `findAll should filter by categoryId`() {
        every { subcategoryJpaRepository.findByFilters(null, 1L) } returns listOf(subcategoryEntity)

        val result = adapter.findAll(null, 1L)

        assertEquals(1, result.size)
        assertEquals(1L, result[0].categoryId)
    }

    @Test
    fun `findAll should return empty list when no results`() {
        every { subcategoryJpaRepository.findByFilters(null, null) } returns emptyList()

        val result = adapter.findAll(null, null)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `findById should return subcategory when found`() {
        every { subcategoryJpaRepository.findById(10L) } returns Optional.of(subcategoryEntity)

        val result = adapter.findById(10L)

        assertEquals(10L, result?.id)
        assertEquals("Snacks", result?.name)
    }

    @Test
    fun `findById should return null when not found`() {
        every { subcategoryJpaRepository.findById(99L) } returns Optional.empty()

        val result = adapter.findById(99L)

        assertNull(result)
    }

    @Test
    fun `save should persist subcategory and return domain model`() {
        val subcategory = Subcategory(id = null, name = "Beverages", categoryId = 1L)
        val savedEntity = SubcategoryEntity(id = 11L, name = "Beverages", category = categoryEntity)
        every { categoryJpaRepository.findById(1L) } returns Optional.of(categoryEntity)
        every { subcategoryJpaRepository.save(any()) } returns savedEntity

        val result = adapter.save(subcategory)

        assertEquals(11L, result.id)
        assertEquals("Beverages", result.name)
        assertEquals(1L, result.categoryId)
        verify { subcategoryJpaRepository.save(any()) }
    }

    @Test
    fun `save should throw CategoryNotFoundException when category not found`() {
        val subcategory = Subcategory(id = null, name = "Beverages", categoryId = 99L)
        every { categoryJpaRepository.findById(99L) } returns Optional.empty()

        assertThrows<CategoryNotFoundException> {
            adapter.save(subcategory)
        }
    }

    @Test
    fun `update should persist updated subcategory and return domain model`() {
        val subcategory = Subcategory(id = 10L, name = "Updated Snacks", categoryId = 1L)
        val updatedEntity = SubcategoryEntity(id = 10L, name = "Updated Snacks", category = categoryEntity)
        every { categoryJpaRepository.findById(1L) } returns Optional.of(categoryEntity)
        every { subcategoryJpaRepository.save(any()) } returns updatedEntity

        val result = adapter.update(subcategory)

        assertEquals(10L, result.id)
        assertEquals("Updated Snacks", result.name)
    }

    @Test
    fun `update should throw CategoryNotFoundException when category not found`() {
        val subcategory = Subcategory(id = 10L, name = "Snacks", categoryId = 99L)
        every { categoryJpaRepository.findById(99L) } returns Optional.empty()

        assertThrows<CategoryNotFoundException> {
            adapter.update(subcategory)
        }
    }

    @Test
    fun `deleteById should delegate to JPA repository`() {
        every { subcategoryJpaRepository.deleteById(10L) } returns Unit

        adapter.deleteById(10L)

        verify { subcategoryJpaRepository.deleteById(10L) }
    }

    @Test
    fun `existsByNameInCategory should return true when subcategory exists`() {
        every { subcategoryJpaRepository.existsByNameAndCategoryId("Snacks", 1L) } returns true

        val result = adapter.existsByNameInCategory("Snacks", 1L)

        assertTrue(result)
    }

    @Test
    fun `existsByNameInCategory should return false when subcategory does not exist`() {
        every { subcategoryJpaRepository.existsByNameAndCategoryId("Unknown", 1L) } returns false

        val result = adapter.existsByNameInCategory("Unknown", 1L)

        assertEquals(false, result)
    }
}
