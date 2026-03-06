package com.heitor.finance.application.usecase

import com.heitor.finance.application.port.output.CategoryOutputPort
import com.heitor.finance.domain.exception.CategoryNotFoundException
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DeleteCategoryUseCaseImplTest {

    private val categoryOutputPort: CategoryOutputPort = mockk()
    private val useCase = DeleteCategoryUseCaseImpl(categoryOutputPort)

    @Test
    fun `should delete category when it exists`() {
        every { categoryOutputPort.existsById(1L) } returns true
        every { categoryOutputPort.deleteById(1L) } returns Unit

        useCase.execute(1L)

        verify { categoryOutputPort.deleteById(1L) }
    }

    @Test
    fun `should throw CategoryNotFoundException when category does not exist`() {
        every { categoryOutputPort.existsById(99L) } returns false

        assertThrows<CategoryNotFoundException> { useCase.execute(99L) }

        verify(exactly = 0) { categoryOutputPort.deleteById(any()) }
    }
}
