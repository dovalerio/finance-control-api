package com.heitor.finance.application.usecase

import com.heitor.finance.application.dto.CreateEntryRequest
import com.heitor.finance.application.port.output.EntryOutputPort
import com.heitor.finance.application.port.output.SubcategoryOutputPort
import com.heitor.finance.domain.exception.SubcategoryNotFoundException
import com.heitor.finance.domain.model.Entry
import com.heitor.finance.domain.model.EntryType
import com.heitor.finance.domain.model.Subcategory
import com.heitor.finance.domain.valueobject.Money
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.LocalDate

class CreateEntryUseCaseImplTest {

    private val entryOutputPort: EntryOutputPort = mockk()
    private val subcategoryOutputPort: SubcategoryOutputPort = mockk()
    private val useCase = CreateEntryUseCaseImpl(entryOutputPort, subcategoryOutputPort)

    private val subcategory = Subcategory(id = 5L, name = "Fuel", categoryId = 2L)
    private val today = LocalDate.now()

    @Test
    fun `should create INCOME entry when value is positive`() {
        val request = CreateEntryRequest(value = BigDecimal("150.00"), subcategoryId = 5L, date = today, comment = "Salary")
        val entrySlot = slot<Entry>()
        val savedEntry = Entry(
            id = 1L,
            description = "Salary",
            amount = Money.of(BigDecimal("150.00")),
            type = EntryType.INCOME,
            date = today,
            categoryId = 2L,
            subcategoryId = 5L
        )

        every { subcategoryOutputPort.findById(5L) } returns subcategory
        every { entryOutputPort.save(capture(entrySlot)) } returns savedEntry

        val response = useCase.execute(request)

        assertEquals(1L, response.id)
        assertEquals(BigDecimal("150.00"), response.value)
        assertEquals(5L, response.subcategoryId)
        assertEquals("Salary", response.comment)

        val captured = entrySlot.captured
        assertEquals(EntryType.INCOME, captured.type)
        assertEquals(Money.of(BigDecimal("150.00")), captured.amount)
    }

    @Test
    fun `should create EXPENSE entry and return negated value when value is negative`() {
        val request = CreateEntryRequest(value = BigDecimal("-80.00"), subcategoryId = 5L, date = today)
        val savedEntry = Entry(
            id = 2L,
            description = "",
            amount = Money.of(BigDecimal("80.00")),
            type = EntryType.EXPENSE,
            date = today,
            categoryId = 2L,
            subcategoryId = 5L
        )

        every { subcategoryOutputPort.findById(5L) } returns subcategory
        every { entryOutputPort.save(any()) } returns savedEntry

        val response = useCase.execute(request)

        assertEquals(2L, response.id)
        assertEquals(BigDecimal("-80.00"), response.value)
        assertEquals(null, response.comment)
    }

    @Test
    fun `should throw SubcategoryNotFoundException when subcategory does not exist`() {
        val request = CreateEntryRequest(value = BigDecimal("50.00"), subcategoryId = 99L, date = today)

        every { subcategoryOutputPort.findById(99L) } returns null

        assertThrows<SubcategoryNotFoundException> { useCase.execute(request) }

        verify(exactly = 0) { entryOutputPort.save(any()) }
    }
}
