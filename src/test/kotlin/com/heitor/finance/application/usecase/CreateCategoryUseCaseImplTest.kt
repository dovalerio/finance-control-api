package com.heitor.finance.application.usecase

import com.heitor.finance.application.dto.CategoryRequest
import com.heitor.finance.application.port.output.CategoryOutputPort
import com.heitor.finance.domain.exception.CategoryAlreadyExistsException
import com.heitor.finance.domain.model.Category
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CreateCategoryUseCaseImplTest {

    private val categoryOutputPort: CategoryOutputPort = mockk()
    private val useCase = CreateCategoryUseCaseImpl(categoryOutputPort)

    @Test
    fun `should create category and return response`() {
        val request = CategoryRequest(name = "Transport")
        val savedCategory = Category(id = 1L, name = "Transport")

        every { categoryOutputPort.existsByName("Transport") } returns false
        every { categoryOutputPort.save(Category(name = "Transport")) } returns savedCategory

        val response = useCase.execute(request)

        assertEquals(1L, response.id)
        assertEquals("Transport", response.name)
    }

    @Test
    fun `should throw CategoryAlreadyExistsException when name already exists`() {
        val request = CategoryRequest(name = "Transport")

        every { categoryOutputPort.existsByName("Transport") } returns true

        assertThrows<CategoryAlreadyExistsException> { useCase.execute(request) }

        verify(exactly = 0) { categoryOutputPort.save(any()) }
    }
}
