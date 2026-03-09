package com.heitor.finance.domain.service

import com.heitor.finance.domain.model.Category
import com.heitor.finance.domain.model.Entry
import com.heitor.finance.domain.model.EntryType
import com.heitor.finance.domain.valueobject.Money
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class BalanceCalculatorServiceTest {

    private val service = BalanceCalculatorService()
    private val category = Category(id = 1L, name = "Transport")
    private val date = LocalDate.of(2024, 1, 15)

    @Test
    fun `should calculate balance correctly for income and expense entries`() {
        val entries = listOf(
            Entry(description = "Bus", amount = Money.of("50.00"), type = EntryType.EXPENSE, date = date, categoryId = 1L),
            Entry(description = "Refund", amount = Money.of("150.00"), type = EntryType.INCOME, date = date, categoryId = 1L)
        )

        val balance = service.calculate(category, entries)

        assertEquals(Money.of("150.00"), balance.income)
        assertEquals(Money.of("50.00"), balance.expense)
        assertEquals(0, balance.net.compareTo(BigDecimal("100.00")))
    }

    @Test
    fun `should return zero balance when no entries are provided`() {
        val balance = service.calculate(category, emptyList())

        assertEquals(Money.ZERO, balance.income)
        assertEquals(Money.ZERO, balance.expense)
        assertEquals(0, balance.net.compareTo(BigDecimal.ZERO))
    }

    @Test
    fun `should return negative net when expenses exceed income`() {
        val entries = listOf(
            Entry(description = "Bus", amount = Money.of("300.00"), type = EntryType.EXPENSE, date = date, categoryId = 1L),
            Entry(description = "Refund", amount = Money.of("100.00"), type = EntryType.INCOME, date = date, categoryId = 1L)
        )

        val balance = service.calculate(category, entries)

        assertEquals(0, balance.net.compareTo(BigDecimal("-200.00")))
    }

    @Test
    fun `should aggregate multiple income entries`() {
        val entries = listOf(
            Entry(description = "Salary", amount = Money.of("3000.00"), type = EntryType.INCOME, date = date, categoryId = 1L),
            Entry(description = "Bonus", amount = Money.of("500.00"), type = EntryType.INCOME, date = date, categoryId = 1L),
            Entry(description = "Freelance", amount = Money.of("200.00"), type = EntryType.INCOME, date = date, categoryId = 1L)
        )

        val balance = service.calculate(category, entries)

        assertEquals(Money.of("3700.00"), balance.income)
        assertEquals(Money.ZERO, balance.expense)
        assertEquals(0, balance.net.compareTo(BigDecimal("3700.00")))
    }

    @Test
    fun `should aggregate multiple expense entries`() {
        val entries = listOf(
            Entry(description = "Rent", amount = Money.of("1200.00"), type = EntryType.EXPENSE, date = date, categoryId = 1L),
            Entry(description = "Groceries", amount = Money.of("350.00"), type = EntryType.EXPENSE, date = date, categoryId = 1L),
            Entry(description = "Utilities", amount = Money.of("80.00"), type = EntryType.EXPENSE, date = date, categoryId = 1L)
        )

        val balance = service.calculate(category, entries)

        assertEquals(Money.ZERO, balance.income)
        assertEquals(Money.of("1630.00"), balance.expense)
        assertEquals(0, balance.net.compareTo(BigDecimal("-1630.00")))
    }

    @Test
    fun `should only income entries result in zero expense`() {
        val entries = listOf(
            Entry(description = "Salary", amount = Money.of("5000.00"), type = EntryType.INCOME, date = date, categoryId = 1L)
        )

        val balance = service.calculate(category, entries)

        assertEquals(0, balance.expense.amount.compareTo(BigDecimal.ZERO))
        assertEquals(Money.of("5000.00"), balance.income)
    }

    @Test
    fun `should only expense entries result in zero income`() {
        val entries = listOf(
            Entry(description = "Rent", amount = Money.of("1500.00"), type = EntryType.EXPENSE, date = date, categoryId = 1L)
        )

        val balance = service.calculate(category, entries)

        assertEquals(0, balance.income.amount.compareTo(BigDecimal.ZERO))
        assertEquals(Money.of("1500.00"), balance.expense)
    }

    @Test
    fun `should bind result to correct category`() {
        val balance = service.calculate(category, emptyList())
        assertEquals(category, balance.category)
    }
}
