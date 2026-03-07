package com.heitor.finance.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SubcategoryTest {

    @Test
    fun `should create subcategory with valid name`() {
        val sub = Subcategory(id = 1L, name = "Bus", categoryId = 1L)
        assertEquals("Bus", sub.name)
        assertEquals(1L, sub.categoryId)
    }

    @Test
    fun `should default id to null`() {
        val sub = Subcategory(name = "Taxi", categoryId = 2L)
        assertNull(sub.id)
    }

    @Test
    fun `should throw when name is blank`() {
        val ex = assertThrows<IllegalArgumentException> { Subcategory(name = "", categoryId = 1L) }
        assertEquals("Subcategory name must not be blank", ex.message)
    }

    @Test
    fun `should throw when name is whitespace only`() {
        assertThrows<IllegalArgumentException> { Subcategory(name = "   ", categoryId = 1L) }
    }
}
