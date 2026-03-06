package com.heitor.finance.domain.service

import com.heitor.finance.domain.model.Category
import com.heitor.finance.domain.model.Entry
import com.heitor.finance.domain.model.EntryType
import com.heitor.finance.domain.valueobject.Money
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

class BalanceCalculatorServiceTest {

    private val service = BalanceCalculatorService()

    @Test
    fun `should calculate balance correctly for income and expense entries`() {
        val category = Category(id = 1L, name = "Transport")
        val entries = listOf(
            Entry(description = "Bus", amount = Money.of("50.00"), type = EntryType.EXPENSE, date = LocalDate.now(), categoryId = 1L),
            Entry(description = "Refund", amount = Money.of("150.00"), type = EntryType.INCOME, date = LocalDate.now(), categoryId = 1L)
        )

        val balance = service.calculate(category, entries)

        assertEquals(Money.of("150.00"), balance.income)
        assertEquals(Money.of("50.00"), balance.expense)
    }

    @Test
    fun `should return zero balance when no entries are provided`() {
        val category = Category(id = 1L, name = "Transport")

        val balance = service.calculate(category, emptyList())

        assertEquals(Money.ZERO, balance.income)
        assertEquals(Money.ZERO, balance.expense)
    }
}
