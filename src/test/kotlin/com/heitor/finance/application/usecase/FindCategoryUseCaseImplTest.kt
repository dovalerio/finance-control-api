package com.heitor.finance.application.usecase

import com.heitor.finance.application.port.output.CategoryOutputPort
import com.heitor.finance.domain.exception.CategoryNotFoundException
import com.heitor.finance.domain.model.Category
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class FindCategoryUseCaseImplTest {

    private val categoryOutputPort: CategoryOutputPort = mockk()
    private val service = FindCategoryUseCaseImpl(categoryOutputPort)

    @Test
    fun `findAll without filter returns all categories`() {
        every { categoryOutputPort.findAll(null) } returns listOf(
            Category(id = 1L, name = "Transport"),
            Category(id = 2L, name = "Food")
        )

        val result = service.findAll(null)

        assertEquals(2, result.size)
        verify { categoryOutputPort.findAll(null) }
    }

    @Test
    fun `findAll with name filter forwards name to output port`() {
        every { categoryOutputPort.findAll("trans") } returns listOf(
            Category(id = 1L, name = "Transport")
        )

        val result = service.findAll("trans")

        assertEquals(1, result.size)
        assertEquals("Transport", result.first().name)
        verify { categoryOutputPort.findAll("trans") }
    }

    @Test
    fun `findAll returns empty list when no categories match`() {
        every { categoryOutputPort.findAll("xyz") } returns emptyList()

        val result = service.findAll("xyz")

        assertEquals(0, result.size)
    }

    @Test
    fun `findById returns category response when found`() {
        every { categoryOutputPort.findById(1L) } returns Category(id = 1L, name = "Transport")

        val result = service.findById(1L)

        assertEquals(1L, result.id)
        assertEquals("Transport", result.name)
        verify { categoryOutputPort.findById(1L) }
    }

    @Test
    fun `findById throws CategoryNotFoundException when not found`() {
        every { categoryOutputPort.findById(99L) } returns null

        assertThrows<CategoryNotFoundException> { service.findById(99L) }
    }
}
