package com.heitor.finance.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SubcategoryTest {

    @Test
    fun `should create subcategory with valid name`() {
        val sub = Subcategory(id = 1L, name = "Bus", categoryId = 1L)
        assertEquals("Bus", sub.name)
    }

    @Test
    fun `should throw when name is blank`() {
        assertThrows<IllegalArgumentException> { Subcategory(name = "", categoryId = 1L) }
    }

    @Test
    fun `should throw when name is whitespace only`() {
        assertThrows<IllegalArgumentException> { Subcategory(name = "   ", categoryId = 1L) }
    }
}
