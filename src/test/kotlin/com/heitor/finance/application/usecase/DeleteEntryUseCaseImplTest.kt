package com.heitor.finance.application.usecase

import com.heitor.finance.application.port.output.EntryOutputPort
import com.heitor.finance.domain.exception.EntryNotFoundException
import com.heitor.finance.domain.model.Entry
import com.heitor.finance.domain.model.EntryType
import com.heitor.finance.domain.valueobject.Money
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.LocalDate

class DeleteEntryUseCaseImplTest {

    private val entryOutputPort: EntryOutputPort = mockk()
    private val useCase = DeleteEntryUseCaseImpl(entryOutputPort)

    private val entry = Entry(
        id = 1L, description = "Salary", amount = Money.of(BigDecimal("3000.00")),
        type = EntryType.INCOME, date = LocalDate.now(), categoryId = 1L, subcategoryId = 5L
    )

    @Test
    fun `should delete entry when it exists`() {
        every { entryOutputPort.findById(1L) } returns entry
        every { entryOutputPort.deleteById(1L) } returns Unit

        useCase.execute(1L)

        verify { entryOutputPort.deleteById(1L) }
    }

    @Test
    fun `should throw EntryNotFoundException when entry does not exist`() {
        every { entryOutputPort.findById(99L) } returns null

        assertThrows<EntryNotFoundException> { useCase.execute(99L) }

        verify(exactly = 0) { entryOutputPort.deleteById(any()) }
    }
}
