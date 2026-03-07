package com.heitor.finance.application.usecase

import com.heitor.finance.application.port.output.SubcategoryOutputPort
import com.heitor.finance.domain.exception.SubcategoryNotFoundException
import com.heitor.finance.domain.model.Subcategory
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DeleteSubcategoryUseCaseImplTest {

    private val subcategoryOutputPort: SubcategoryOutputPort = mockk()
    private val useCase = DeleteSubcategoryUseCaseImpl(subcategoryOutputPort)

    @Test
    fun `should delete subcategory when it exists`() {
        val subcategory = Subcategory(id = 5L, name = "Fuel", categoryId = 2L)

        every { subcategoryOutputPort.findById(5L) } returns subcategory
        every { subcategoryOutputPort.deleteById(5L) } returns Unit

        useCase.execute(5L)

        verify { subcategoryOutputPort.deleteById(5L) }
    }

    @Test
    fun `should throw SubcategoryNotFoundException when subcategory does not exist`() {
        every { subcategoryOutputPort.findById(99L) } returns null

        assertThrows<SubcategoryNotFoundException> { useCase.execute(99L) }

        verify(exactly = 0) { subcategoryOutputPort.deleteById(any()) }
    }
}
