package com.heitor.finance.domain.model

import com.heitor.finance.domain.valueobject.Money
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class BalanceTest {

    private val category = Category(id = 1L, name = "Transport")

    @Test
    fun `should compute net as income minus expense`() {
        val balance = Balance(
            category = category,
            income = Money.of("200.00"),
            expense = Money.of("80.00")
        )
        assertEquals(0, balance.net.compareTo(BigDecimal("120.00")))
    }

    @Test
    fun `should return negative net when expense exceeds income`() {
        val balance = Balance(
            category = category,
            income = Money.of("50.00"),
            expense = Money.of("200.00")
        )
        assertEquals(0, balance.net.compareTo(BigDecimal("-150.00")))
    }

    @Test
    fun `should expose income and expense separately`() {
        val balance = Balance(
            category = category,
            income = Money.of("300.00"),
            expense = Money.of("120.00")
        )
        assertEquals(Money.of("300.00"), balance.income)
        assertEquals(Money.of("120.00"), balance.expense)
    }

    @Test
    fun `should return zero net when no entries`() {
        val balance = Balance(
            category = category,
            income = Money.ZERO,
            expense = Money.ZERO
        )
        assertEquals(0, balance.net.compareTo(BigDecimal.ZERO))
    }

    @Test
    fun `should store category reference`() {
        val balance = Balance(
            category = category,
            income = Money.of("100.00"),
            expense = Money.of("40.00")
        )
        assertEquals("Transport", balance.category.name)
        assertEquals(1L, balance.category.id)
    }

    @Test
    fun `should accept explicitly overridden net`() {
        val customNet = BigDecimal("999.00")
        val balance = Balance(
            category = category,
            income = Money.of("100.00"),
            expense = Money.of("40.00"),
            net = customNet
        )
        assertEquals(0, balance.net.compareTo(customNet))
    }
}
