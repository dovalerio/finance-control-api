package com.heitor.finance.application.usecase

import com.heitor.finance.application.dto.CategoryRequest
import com.heitor.finance.application.port.output.CategoryOutputPort
import com.heitor.finance.domain.exception.CategoryAlreadyExistsException
import com.heitor.finance.domain.exception.CategoryNotFoundException
import com.heitor.finance.domain.model.Category
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UpdateCategoryUseCaseImplTest {

    private val categoryOutputPort: CategoryOutputPort = mockk()
    private val useCase = UpdateCategoryUseCaseImpl(categoryOutputPort)

    @Test
    fun `should update category name and return response`() {
        val existing = Category(id = 1L, name = "Transport")
        val updated = Category(id = 1L, name = "Food")
        val request = CategoryRequest(name = "Food")

        every { categoryOutputPort.findById(1L) } returns existing
        every { categoryOutputPort.existsByName("Food") } returns false
        every { categoryOutputPort.update(updated) } returns updated

        val response = useCase.execute(1L, request)

        assertEquals(1L, response.id)
        assertEquals("Food", response.name)
        verify { categoryOutputPort.update(updated) }
    }

    @Test
    fun `should allow update when name is unchanged`() {
        val existing = Category(id = 1L, name = "Transport")
        val request = CategoryRequest(name = "Transport")

        every { categoryOutputPort.findById(1L) } returns existing
        every { categoryOutputPort.update(existing) } returns existing

        val response = useCase.execute(1L, request)

        assertEquals("Transport", response.name)
        verify(exactly = 0) { categoryOutputPort.existsByName(any()) }
    }

    @Test
    fun `should throw CategoryNotFoundException when category does not exist`() {
        every { categoryOutputPort.findById(99L) } returns null

        assertThrows<CategoryNotFoundException> {
            useCase.execute(99L, CategoryRequest(name = "Food"))
        }

        verify(exactly = 0) { categoryOutputPort.update(any()) }
    }

    @Test
    fun `should throw CategoryAlreadyExistsException when new name is taken`() {
        val existing = Category(id = 1L, name = "Transport")
        val request = CategoryRequest(name = "Food")

        every { categoryOutputPort.findById(1L) } returns existing
        every { categoryOutputPort.existsByName("Food") } returns true

        assertThrows<CategoryAlreadyExistsException> { useCase.execute(1L, request) }

        verify(exactly = 0) { categoryOutputPort.update(any()) }
    }
}
