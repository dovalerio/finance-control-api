package com.heitor.finance.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CategoryTest {

    @Test
    fun `should create category with valid name`() {
        val category = Category(id = 1L, name = "Transport")
        assertEquals("Transport", category.name)
    }

    @Test
    fun `should create category with subcategories having unique names`() {
        val sub1 = Subcategory(name = "Bus", categoryId = 1L)
        val sub2 = Subcategory(name = "Train", categoryId = 1L)
        val category = Category(id = 1L, name = "Transport", subcategories = listOf(sub1, sub2))
        assertEquals(2, category.subcategories.size)
    }

    @Test
    fun `should throw when name is blank`() {
        assertThrows<IllegalArgumentException> { Category(name = "") }
    }

    @Test
    fun `should throw when name is whitespace only`() {
        assertThrows<IllegalArgumentException> { Category(name = "   ") }
    }

    @Test
    fun `should throw when subcategory names are duplicated within category`() {
        val sub1 = Subcategory(name = "Bus", categoryId = 1L)
        val sub2 = Subcategory(name = "Bus", categoryId = 1L)
        assertThrows<IllegalArgumentException> {
            Category(id = 1L, name = "Transport", subcategories = listOf(sub1, sub2))
        }
    }

    @Test
    fun `should throw when subcategory names differ only in case`() {
        val sub1 = Subcategory(name = "Bus", categoryId = 1L)
        val sub2 = Subcategory(name = "bus", categoryId = 1L)
        assertThrows<IllegalArgumentException> {
            Category(id = 1L, name = "Transport", subcategories = listOf(sub1, sub2))
        }
    }
}
