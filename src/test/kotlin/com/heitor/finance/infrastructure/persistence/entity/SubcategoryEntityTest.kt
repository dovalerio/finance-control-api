package com.heitor.finance.infrastructure.persistence.entity

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class SubcategoryEntityTest {

    private val parentCategory = CategoryEntity(id = 1L, name = "Food")

    @Test
    fun `should create SubcategoryEntity with all fields`() {
        val entity = SubcategoryEntity(id = 10L, name = "Snacks", category = parentCategory)

        assertEquals(10L, entity.id)
        assertEquals("Snacks", entity.name)
        assertEquals(1L, entity.category.id)
        assertEquals("Food", entity.category.name)
    }

    @Test
    fun `should create SubcategoryEntity with null id`() {
        val entity = SubcategoryEntity(name = "Beverages", category = parentCategory)

        assertNull(entity.id)
        assertEquals("Beverages", entity.name)
        assertEquals(parentCategory, entity.category)
    }

    @Test
    fun `should reference parent category correctly`() {
        val category = CategoryEntity(id = 5L, name = "Health")
        val entity = SubcategoryEntity(id = 20L, name = "Pharmacy", category = category)

        assertEquals(5L, entity.category.id)
        assertEquals("Health", entity.category.name)
    }
}
