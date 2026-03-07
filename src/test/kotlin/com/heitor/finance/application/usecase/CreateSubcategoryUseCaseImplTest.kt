package com.heitor.finance.application.usecase

import com.heitor.finance.application.dto.CreateSubcategoryRequest
import com.heitor.finance.application.port.output.CategoryOutputPort
import com.heitor.finance.application.port.output.SubcategoryOutputPort
import com.heitor.finance.domain.exception.CategoryNotFoundException
import com.heitor.finance.domain.exception.SubcategoryAlreadyExistsException
import com.heitor.finance.domain.model.Subcategory
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CreateSubcategoryUseCaseImplTest {

    private val subcategoryOutputPort: SubcategoryOutputPort = mockk()
    private val categoryOutputPort: CategoryOutputPort = mockk()
    private val useCase = CreateSubcategoryUseCaseImpl(subcategoryOutputPort, categoryOutputPort)

    @Test
    fun `should create subcategory and return response`() {
        val request = CreateSubcategoryRequest(name = "Fuel", categoryId = 1L)
        val saved = Subcategory(id = 10L, name = "Fuel", categoryId = 1L)

        every { categoryOutputPort.existsById(1L) } returns true
        every { subcategoryOutputPort.existsByNameInCategory("Fuel", 1L) } returns false
        every { subcategoryOutputPort.save(Subcategory(name = "Fuel", categoryId = 1L)) } returns saved

        val response = useCase.execute(request)

        assertEquals(10L, response.id)
        assertEquals("Fuel", response.name)
        assertEquals(1L, response.categoryId)
    }

    @Test
    fun `should throw CategoryNotFoundException when category does not exist`() {
        val request = CreateSubcategoryRequest(name = "Fuel", categoryId = 99L)

        every { categoryOutputPort.existsById(99L) } returns false

        assertThrows<CategoryNotFoundException> { useCase.execute(request) }

        verify(exactly = 0) { subcategoryOutputPort.save(any()) }
    }

    @Test
    fun `should throw SubcategoryAlreadyExistsException when name already exists in category`() {
        val request = CreateSubcategoryRequest(name = "Fuel", categoryId = 1L)

        every { categoryOutputPort.existsById(1L) } returns true
        every { subcategoryOutputPort.existsByNameInCategory("Fuel", 1L) } returns true

        assertThrows<SubcategoryAlreadyExistsException> { useCase.execute(request) }

        verify(exactly = 0) { subcategoryOutputPort.save(any()) }
    }
}
