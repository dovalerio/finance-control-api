package com.heitor.finance.application.usecase

import com.heitor.finance.application.dto.CreateCategoryRequest
import com.heitor.finance.application.port.output.CategoryOutputPort
import com.heitor.finance.domain.model.Category
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CreateCategoryUseCaseImplTest {

    private val categoryOutputPort: CategoryOutputPort = mockk()
    private val useCase = CreateCategoryUseCaseImpl(categoryOutputPort)

    @Test
    fun `should create category and return response`() {
        val request = CreateCategoryRequest(name = "Transport")
        val savedCategory = Category(id = 1L, name = "Transport")

        every { categoryOutputPort.save(Category(name = "Transport")) } returns savedCategory

        val response = useCase.execute(request)

        assertEquals(1L, response.id)
        assertEquals("Transport", response.name)
    }
}
