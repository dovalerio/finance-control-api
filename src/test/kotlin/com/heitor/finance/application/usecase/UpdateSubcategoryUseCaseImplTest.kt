package com.heitor.finance.application.usecase

import com.heitor.finance.application.dto.CreateSubcategoryRequest
import com.heitor.finance.application.port.output.CategoryOutputPort
import com.heitor.finance.application.port.output.SubcategoryOutputPort
import com.heitor.finance.domain.exception.CategoryNotFoundException
import com.heitor.finance.domain.exception.SubcategoryAlreadyExistsException
import com.heitor.finance.domain.exception.SubcategoryNotFoundException
import com.heitor.finance.domain.model.Subcategory
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UpdateSubcategoryUseCaseImplTest {

    private val subcategoryOutputPort: SubcategoryOutputPort = mockk()
    private val categoryOutputPort: CategoryOutputPort = mockk()
    private val useCase = UpdateSubcategoryUseCaseImpl(subcategoryOutputPort, categoryOutputPort)

    private val existing = Subcategory(id = 5L, name = "Fuel", categoryId = 2L)

    @Test
    fun `should update subcategory successfully`() {
        val request = CreateSubcategoryRequest(name = "Petrol", categoryId = 2L)
        val updated = Subcategory(id = 5L, name = "Petrol", categoryId = 2L)

        every { subcategoryOutputPort.findById(5L) } returns existing
        every { categoryOutputPort.existsById(2L) } returns true
        every { subcategoryOutputPort.existsByNameInCategory("Petrol", 2L) } returns false
        every { subcategoryOutputPort.update(any()) } returns updated

        val result = useCase.execute(5L, request)

        assertEquals(5L, result.id)
        assertEquals("Petrol", result.name)
        assertEquals(2L, result.categoryId)
        verify { subcategoryOutputPort.update(any()) }
    }

    @Test
    fun `should allow update when name is unchanged`() {
        val request = CreateSubcategoryRequest(name = "Fuel", categoryId = 2L)
        val updated = Subcategory(id = 5L, name = "Fuel", categoryId = 2L)

        every { subcategoryOutputPort.findById(5L) } returns existing
        every { categoryOutputPort.existsById(2L) } returns true
        every { subcategoryOutputPort.update(any()) } returns updated

        val result = useCase.execute(5L, request)

        assertEquals("Fuel", result.name)
        verify(exactly = 0) { subcategoryOutputPort.existsByNameInCategory(any(), any()) }
    }

    @Test
    fun `should throw SubcategoryNotFoundException when subcategory does not exist`() {
        val request = CreateSubcategoryRequest(name = "Petrol", categoryId = 2L)
        every { subcategoryOutputPort.findById(99L) } returns null

        assertThrows<SubcategoryNotFoundException> { useCase.execute(99L, request) }

        verify(exactly = 0) { subcategoryOutputPort.update(any()) }
    }

    @Test
    fun `should throw CategoryNotFoundException when category does not exist`() {
        val request = CreateSubcategoryRequest(name = "Petrol", categoryId = 99L)

        every { subcategoryOutputPort.findById(5L) } returns existing
        every { categoryOutputPort.existsById(99L) } returns false

        assertThrows<CategoryNotFoundException> { useCase.execute(5L, request) }
    }

    @Test
    fun `should throw SubcategoryAlreadyExistsException when name is taken in category`() {
        val request = CreateSubcategoryRequest(name = "Groceries", categoryId = 2L)

        every { subcategoryOutputPort.findById(5L) } returns existing
        every { categoryOutputPort.existsById(2L) } returns true
        every { subcategoryOutputPort.existsByNameInCategory("Groceries", 2L) } returns true

        assertThrows<SubcategoryAlreadyExistsException> { useCase.execute(5L, request) }
    }
}
