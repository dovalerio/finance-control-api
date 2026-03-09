package com.heitor.finance.application.usecase

import com.heitor.finance.application.port.output.CategoryOutputPort
import com.heitor.finance.application.port.output.EntryOutputPort
import com.heitor.finance.domain.exception.CategoryNotFoundException
import com.heitor.finance.domain.exception.InvalidPeriodException
import com.heitor.finance.domain.model.Category
import com.heitor.finance.domain.model.Entry
import com.heitor.finance.domain.model.EntryType
import com.heitor.finance.domain.service.BalanceCalculatorService
import com.heitor.finance.domain.valueobject.Money
import com.heitor.finance.domain.valueobject.Period
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Assertions.assertNotNull
import java.math.BigDecimal
import java.time.LocalDate

class FindBalanceUseCaseImplTest {

    private val categoryOutputPort: CategoryOutputPort = mockk()
    private val entryOutputPort: EntryOutputPort = mockk()
    private val balanceCalculatorService = BalanceCalculatorService()
    private val useCase = FindBalanceUseCaseImpl(categoryOutputPort, entryOutputPort, balanceCalculatorService)

    private val start = LocalDate.of(2024, 1, 1)
    private val end = LocalDate.of(2024, 1, 31)
    private val period = Period(start, end)

    @Test
    fun `should return balance with category when categoryId provided`() {
        val category = Category(id = 1L, name = "Transport")
        val entries = listOf(
            Entry(comment ="Bus", amount = Money.of("100.00"), type = EntryType.EXPENSE, date = start, categoryId = 1L),
            Entry(comment ="Refund", amount = Money.of("200.00"), type = EntryType.INCOME, date = start, categoryId = 1L)
        )

        every { categoryOutputPort.findById(1L) } returns category
        every { entryOutputPort.findByPeriodAndCategoryId(period, 1L) } returns entries

        val response = useCase.findByPeriodAndCategory(start, end, 1L)

        assertEquals(1L, response.category?.id)
        assertEquals(BigDecimal("200.00"), response.revenue)
        assertEquals(BigDecimal("100.00"), response.expense)
        assertEquals(BigDecimal("100.00"), response.balance)
    }

    @Test
    fun `should return balance without category when categoryId is null`() {
        val entries = listOf(
            Entry(comment ="Salary", amount = Money.of("500.00"), type = EntryType.INCOME, date = start, categoryId = 1L),
            Entry(comment ="Food", amount = Money.of("150.00"), type = EntryType.EXPENSE, date = start, categoryId = 2L)
        )

        every { entryOutputPort.findByPeriod(period) } returns entries

        val response = useCase.findByPeriodAndCategory(start, end, null)

        assertNull(response.category)
        assertEquals(BigDecimal("500.00"), response.revenue)
        assertEquals(BigDecimal("150.00"), response.expense)
        assertEquals(BigDecimal("350.00"), response.balance)
    }

    @Test
    fun `should throw CategoryNotFoundException when categoryId provided but category does not exist`() {
        every { categoryOutputPort.findById(99L) } returns null

        assertThrows<CategoryNotFoundException> {
            useCase.findByPeriodAndCategory(start, end, 99L)
        }
    }

    @Test
    fun `should throw InvalidPeriodException when startDate is after endDate`() {
        val invalidStart = LocalDate.of(2024, 2, 1)
        val invalidEnd = LocalDate.of(2024, 1, 1)

        assertThrows<InvalidPeriodException> {
            useCase.findByPeriodAndCategory(invalidStart, invalidEnd, null)
        }
    }

    @Test
    fun `should throw InvalidPeriodException with descriptive message`() {
        val invalidStart = LocalDate.of(2024, 12, 31)
        val invalidEnd = LocalDate.of(2024, 1, 1)

        val ex = assertThrows<InvalidPeriodException> {
            useCase.findByPeriodAndCategory(invalidStart, invalidEnd, null)
        }
        assertNotNull(ex.message)
    }
}
