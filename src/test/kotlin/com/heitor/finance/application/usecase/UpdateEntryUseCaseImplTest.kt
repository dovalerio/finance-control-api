package com.heitor.finance.application.usecase

import com.heitor.finance.application.dto.CreateEntryRequest
import com.heitor.finance.application.port.output.EntryOutputPort
import com.heitor.finance.application.port.output.SubcategoryOutputPort
import com.heitor.finance.domain.exception.EntryNotFoundException
import com.heitor.finance.domain.exception.SubcategoryNotFoundException
import com.heitor.finance.domain.model.Entry
import com.heitor.finance.domain.model.EntryType
import com.heitor.finance.domain.model.Subcategory
import com.heitor.finance.domain.valueobject.Money
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.LocalDate

class UpdateEntryUseCaseImplTest {

    private val entryOutputPort: EntryOutputPort = mockk()
    private val subcategoryOutputPort: SubcategoryOutputPort = mockk()
    private val useCase = UpdateEntryUseCaseImpl(entryOutputPort, subcategoryOutputPort)

    private val date = LocalDate.of(2024, 3, 10)
    private val subcategory = Subcategory(id = 5L, name = "Fuel", categoryId = 2L)
    private val existingEntry = Entry(
        id = 1L, comment ="Old desc", amount = Money.of(BigDecimal("100.00")),
        type = EntryType.INCOME, date = date, categoryId = 2L, subcategoryId = 5L
    )

    @Test
    fun `should update entry as INCOME when value is positive`() {
        val request = CreateEntryRequest(value = BigDecimal("200.00"), subcategoryId = 5L, date = date, comment = "Updated")
        val updated = existingEntry.copy(amount = Money.of(BigDecimal("200.00")), comment ="Updated")

        every { entryOutputPort.findById(1L) } returns existingEntry
        every { subcategoryOutputPort.findById(5L) } returns subcategory
        every { entryOutputPort.save(any()) } returns updated

        val result = useCase.execute(1L, request)

        assertEquals(BigDecimal("200.00"), result.value)
        assertEquals("Updated", result.comment)
        verify { entryOutputPort.save(any()) }
    }

    @Test
    fun `should update entry as EXPENSE and return negative value when value is negative`() {
        val request = CreateEntryRequest(value = BigDecimal("-75.00"), subcategoryId = 5L, date = date)
        val updated = existingEntry.copy(
            amount = Money.of(BigDecimal("75.00")), type = EntryType.EXPENSE, comment =""
        )

        every { entryOutputPort.findById(1L) } returns existingEntry
        every { subcategoryOutputPort.findById(5L) } returns subcategory
        every { entryOutputPort.save(any()) } returns updated

        val result = useCase.execute(1L, request)

        assertEquals(BigDecimal("-75.00"), result.value)
    }

    @Test
    fun `should use existing date when request date is null`() {
        val request = CreateEntryRequest(value = BigDecimal("50.00"), subcategoryId = 5L, date = null)
        val updated = existingEntry.copy(amount = Money.of(BigDecimal("50.00")))

        every { entryOutputPort.findById(1L) } returns existingEntry
        every { subcategoryOutputPort.findById(5L) } returns subcategory
        every { entryOutputPort.save(any()) } returns updated

        useCase.execute(1L, request)

        verify { entryOutputPort.save(match { it.date == date }) }
    }

    @Test
    fun `should throw EntryNotFoundException when entry does not exist`() {
        val request = CreateEntryRequest(value = BigDecimal("50.00"), subcategoryId = 5L)
        every { entryOutputPort.findById(99L) } returns null

        assertThrows<EntryNotFoundException> { useCase.execute(99L, request) }

        verify(exactly = 0) { entryOutputPort.save(any()) }
    }

    @Test
    fun `should throw SubcategoryNotFoundException when subcategory does not exist`() {
        val request = CreateEntryRequest(value = BigDecimal("50.00"), subcategoryId = 99L)

        every { entryOutputPort.findById(1L) } returns existingEntry
        every { subcategoryOutputPort.findById(99L) } returns null

        assertThrows<SubcategoryNotFoundException> { useCase.execute(1L, request) }
    }
}
