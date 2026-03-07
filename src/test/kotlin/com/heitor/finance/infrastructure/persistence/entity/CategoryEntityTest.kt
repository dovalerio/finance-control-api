package com.heitor.finance.infrastructure.persistence.entity

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CategoryEntityTest {

    @Test
    fun `should create CategoryEntity with all fields`() {
        val entity = CategoryEntity(id = 1L, name = "Food")

        assertEquals(1L, entity.id)
        assertEquals("Food", entity.name)
        assertTrue(entity.subcategories.isEmpty())
    }

    @Test
    fun `should create CategoryEntity with null id`() {
        val entity = CategoryEntity(name = "New Category")

        assertNull(entity.id)
        assertEquals("New Category", entity.name)
    }

    @Test
    fun `should create CategoryEntity with subcategories`() {
        val parent = CategoryEntity(id = 1L, name = "Food")
        val sub1 = SubcategoryEntity(id = 10L, name = "Snacks", category = parent)
        val sub2 = SubcategoryEntity(id = 11L, name = "Beverages", category = parent)
        val entity = CategoryEntity(id = 1L, name = "Food", subcategories = listOf(sub1, sub2))

        assertEquals(2, entity.subcategories.size)
        assertEquals("Snacks", entity.subcategories[0].name)
        assertEquals("Beverages", entity.subcategories[1].name)
    }

    @Test
    fun `should create CategoryEntity with empty subcategories by default`() {
        val entity = CategoryEntity(id = 2L, name = "Transport")

        assertEquals(0, entity.subcategories.size)
    }
}
