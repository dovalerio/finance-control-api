package com.heitor.finance.application.usecase

import com.heitor.finance.application.port.output.EntryOutputPort
import com.heitor.finance.domain.exception.EntryNotFoundException
import com.heitor.finance.domain.model.Entry
import com.heitor.finance.domain.model.EntryType
import com.heitor.finance.domain.valueobject.Money
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.LocalDate

class FindEntryUseCaseImplTest {

    private val entryOutputPort: EntryOutputPort = mockk()
    private val useCase = FindEntryUseCaseImpl(entryOutputPort)

    private val date = LocalDate.of(2024, 3, 10)
    private val incomeEntry = Entry(
        id = 1L, comment ="Salary", amount = Money.of(BigDecimal("3000.00")),
        type = EntryType.INCOME, date = date, categoryId = 1L, subcategoryId = 5L
    )
    private val expenseEntry = Entry(
        id = 2L, comment ="", amount = Money.of(BigDecimal("50.00")),
        type = EntryType.EXPENSE, date = date, categoryId = 1L, subcategoryId = 5L
    )

    @Test
    fun `findAll should return all entries when no filters`() {
        every { entryOutputPort.findByFilters(null, null, null) } returns listOf(incomeEntry, expenseEntry)

        val result = useCase.findAll(null, null, null)

        assertEquals(2, result.size)
        assertEquals(BigDecimal("3000.00"), result[0].value)
        assertEquals(BigDecimal("-50.00"), result[1].value)
    }

    @Test
    fun `findAll should return positive value for INCOME`() {
        every { entryOutputPort.findByFilters(null, null, null) } returns listOf(incomeEntry)

        val result = useCase.findAll(null, null, null)

        assertEquals(BigDecimal("3000.00"), result[0].value)
    }

    @Test
    fun `findAll should return negative value for EXPENSE`() {
        every { entryOutputPort.findByFilters(null, null, null) } returns listOf(expenseEntry)

        val result = useCase.findAll(null, null, null)

        assertEquals(BigDecimal("-50.00"), result[0].value)
    }

    @Test
    fun `findAll should forward subcategoryId filter`() {
        every { entryOutputPort.findByFilters(5L, null, null) } returns listOf(incomeEntry)

        val result = useCase.findAll(5L, null, null)

        assertEquals(1, result.size)
    }

    @Test
    fun `findAll should forward date range filter`() {
        val start = LocalDate.of(2024, 3, 1)
        val end = LocalDate.of(2024, 3, 31)
        every { entryOutputPort.findByFilters(null, start, end) } returns listOf(incomeEntry)

        val result = useCase.findAll(null, start, end)

        assertEquals(1, result.size)
    }

    @Test
    fun `findAll should forward all filters combined`() {
        val start = LocalDate.of(2024, 3, 1)
        val end = LocalDate.of(2024, 3, 31)
        every { entryOutputPort.findByFilters(5L, start, end) } returns listOf(incomeEntry)

        val result = useCase.findAll(5L, start, end)

        assertEquals(1, result.size)
    }

    @Test
    fun `findAll should map blank description to null comment`() {
        every { entryOutputPort.findByFilters(null, null, null) } returns listOf(expenseEntry)

        val result = useCase.findAll(null, null, null)

        assertNull(result[0].comment)
    }

    @Test
    fun `findById should return entry when found`() {
        every { entryOutputPort.findById(1L) } returns incomeEntry

        val result = useCase.findById(1L)

        assertEquals(1L, result.id)
        assertEquals(BigDecimal("3000.00"), result.value)
        assertEquals("Salary", result.comment)
    }

    @Test
    fun `findById should throw EntryNotFoundException when not found`() {
        every { entryOutputPort.findById(99L) } returns null

        assertThrows<EntryNotFoundException> { useCase.findById(99L) }
    }
}
