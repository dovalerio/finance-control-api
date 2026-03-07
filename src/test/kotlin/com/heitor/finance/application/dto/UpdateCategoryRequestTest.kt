package com.heitor.finance.application.dto

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class UpdateCategoryRequestTest {

    @Test
    fun `should create UpdateCategoryRequest with name`() {
        val request = UpdateCategoryRequest(name = "Health")

        assertEquals("Health", request.name)
    }

    @Test
    fun `should support equals for identical requests`() {
        val r1 = UpdateCategoryRequest(name = "Health")
        val r2 = UpdateCategoryRequest(name = "Health")

        assertEquals(r1, r2)
    }

    @Test
    fun `should support equals for different requests`() {
        val r1 = UpdateCategoryRequest(name = "Health")
        val r2 = UpdateCategoryRequest(name = "Transport")

        assertNotEquals(r1, r2)
    }

    @Test
    fun `should support hashCode consistency`() {
        val r1 = UpdateCategoryRequest(name = "Health")
        val r2 = UpdateCategoryRequest(name = "Health")

        assertEquals(r1.hashCode(), r2.hashCode())
    }

    @Test
    fun `should support copy`() {
        val original = UpdateCategoryRequest(name = "Food")
        val copy = original.copy(name = "Transport")

        assertEquals("Transport", copy.name)
        assertEquals("Food", original.name)
    }

    @Test
    fun `toString should contain field value`() {
        val request = UpdateCategoryRequest(name = "Health")

        val str = request.toString()

        assertEquals(true, str.contains("Health"))
    }
}
