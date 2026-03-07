package com.heitor.finance.infrastructure.persistence.adapter

import com.heitor.finance.domain.model.Category
import com.heitor.finance.infrastructure.persistence.entity.CategoryEntity
import com.heitor.finance.infrastructure.persistence.entity.SubcategoryEntity
import com.heitor.finance.infrastructure.persistence.repository.CategoryJpaRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.Optional

class CategoryRepositoryAdapterTest {

    private val categoryJpaRepository = mockk<CategoryJpaRepository>()
    private val adapter = CategoryRepositoryAdapter(categoryJpaRepository)

    private val categoryEntity = CategoryEntity(id = 1L, name = "Food")
    private val categoryEntityWithSubs = CategoryEntity(
        id = 1L, name = "Food",
        subcategories = listOf(SubcategoryEntity(id = 10L, name = "Snacks", category = CategoryEntity(id = 1L, name = "Food")))
    )

    @Test
    fun `findAll should return list of categories without filter`() {
        every { categoryJpaRepository.findAllWithSubcategories() } returns listOf(categoryEntityWithSubs)

        val result = adapter.findAll(null)

        assertEquals(1, result.size)
        assertEquals(1L, result[0].id)
        assertEquals("Food", result[0].name)
        assertEquals(1, result[0].subcategories.size)
    }

    @Test
    fun `findAll should return filtered categories by name`() {
        every { categoryJpaRepository.findAllWithSubcategoriesByName("Food") } returns listOf(categoryEntity)

        val result = adapter.findAll("Food")

        assertEquals(1, result.size)
        assertEquals("Food", result[0].name)
    }

    @Test
    fun `findAll should return empty list when no categories found`() {
        every { categoryJpaRepository.findAllWithSubcategories() } returns emptyList()

        val result = adapter.findAll(null)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `findById should return category when found`() {
        every { categoryJpaRepository.findByIdWithSubcategories(1L) } returns Optional.of(categoryEntityWithSubs)

        val result = adapter.findById(1L)

        assertEquals(1L, result?.id)
        assertEquals("Food", result?.name)
    }

    @Test
    fun `findById should return null when not found`() {
        every { categoryJpaRepository.findByIdWithSubcategories(99L) } returns Optional.empty()

        val result = adapter.findById(99L)

        assertNull(result)
    }

    @Test
    fun `save should persist category and return domain model`() {
        val category = Category(id = null, name = "Transport")
        val savedEntity = CategoryEntity(id = 2L, name = "Transport")
        every { categoryJpaRepository.save(any()) } returns savedEntity

        val result = adapter.save(category)

        assertEquals(2L, result.id)
        assertEquals("Transport", result.name)
        verify { categoryJpaRepository.save(any()) }
    }

    @Test
    fun `update should persist updated category and return domain model`() {
        val category = Category(id = 1L, name = "Updated Food")
        val updatedEntity = CategoryEntity(id = 1L, name = "Updated Food")
        every { categoryJpaRepository.save(any()) } returns updatedEntity

        val result = adapter.update(category)

        assertEquals(1L, result.id)
        assertEquals("Updated Food", result.name)
        verify { categoryJpaRepository.save(any()) }
    }

    @Test
    fun `deleteById should delegate to JPA repository`() {
        every { categoryJpaRepository.deleteById(1L) } returns Unit

        adapter.deleteById(1L)

        verify { categoryJpaRepository.deleteById(1L) }
    }

    @Test
    fun `existsById should return true when category exists`() {
        every { categoryJpaRepository.existsById(1L) } returns true

        val result = adapter.existsById(1L)

        assertTrue(result)
    }

    @Test
    fun `existsById should return false when category does not exist`() {
        every { categoryJpaRepository.existsById(99L) } returns false

        val result = adapter.existsById(99L)

        assertEquals(false, result)
    }

    @Test
    fun `existsByName should return true when name is taken`() {
        every { categoryJpaRepository.existsByName("Food") } returns true

        val result = adapter.existsByName("Food")

        assertTrue(result)
    }

    @Test
    fun `existsByName should return false when name is available`() {
        every { categoryJpaRepository.existsByName("Unknown") } returns false

        val result = adapter.existsByName("Unknown")

        assertEquals(false, result)
    }
}
