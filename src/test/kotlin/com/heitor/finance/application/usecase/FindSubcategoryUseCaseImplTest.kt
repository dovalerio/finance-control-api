package com.heitor.finance.application.usecase

import com.heitor.finance.application.port.output.SubcategoryOutputPort
import com.heitor.finance.domain.exception.SubcategoryNotFoundException
import com.heitor.finance.domain.model.Subcategory
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class FindSubcategoryUseCaseImplTest {

    private val subcategoryOutputPort: SubcategoryOutputPort = mockk()
    private val useCase = FindSubcategoryUseCaseImpl(subcategoryOutputPort)

    private val subcategory = Subcategory(id = 5L, name = "Fuel", categoryId = 2L)

    @Test
    fun `findAll should return all subcategories when no filters`() {
        every { subcategoryOutputPort.findAll(null, null) } returns listOf(subcategory)

        val result = useCase.findAll(null, null)

        assertEquals(1, result.size)
        assertEquals(5L, result[0].id)
        assertEquals("Fuel", result[0].name)
        assertEquals(2L, result[0].categoryId)
    }

    @Test
    fun `findAll should return empty list when no subcategories`() {
        every { subcategoryOutputPort.findAll(null, null) } returns emptyList()

        val result = useCase.findAll(null, null)

        assertEquals(0, result.size)
    }

    @Test
    fun `findAll should forward name filter to output port`() {
        every { subcategoryOutputPort.findAll("Fuel", null) } returns listOf(subcategory)

        val result = useCase.findAll("Fuel", null)

        assertEquals(1, result.size)
        assertEquals("Fuel", result[0].name)
    }

    @Test
    fun `findAll should forward categoryId filter to output port`() {
        every { subcategoryOutputPort.findAll(null, 2L) } returns listOf(subcategory)

        val result = useCase.findAll(null, 2L)

        assertEquals(1, result.size)
        assertEquals(2L, result[0].categoryId)
    }

    @Test
    fun `findAll should forward both name and categoryId filters`() {
        every { subcategoryOutputPort.findAll("Fuel", 2L) } returns listOf(subcategory)

        val result = useCase.findAll("Fuel", 2L)

        assertEquals(1, result.size)
    }

    @Test
    fun `findById should return subcategory when found`() {
        every { subcategoryOutputPort.findById(5L) } returns subcategory

        val result = useCase.findById(5L)

        assertEquals(5L, result.id)
        assertEquals("Fuel", result.name)
        assertEquals(2L, result.categoryId)
    }

    @Test
    fun `findById should throw SubcategoryNotFoundException when not found`() {
        every { subcategoryOutputPort.findById(99L) } returns null

        assertThrows<SubcategoryNotFoundException> { useCase.findById(99L) }
    }
}
