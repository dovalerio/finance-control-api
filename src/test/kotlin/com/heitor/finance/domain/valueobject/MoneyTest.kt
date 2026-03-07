package com.heitor.finance.domain.valueobject

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class MoneyTest {

    @Test
    fun `should sum two money values`() {
        val result = Money.of("100.00") + Money.of("50.00")
        assertEquals(Money.of("150.00"), result)
    }

    @Test
    fun `should return zero when subtracting larger value`() {
        val result = Money.of("50.00") - Money.of("100.00")
        assertEquals(Money.ZERO, result)
    }

    @Test
    fun `should throw exception when creating money with negative value`() {
        assertThrows<IllegalArgumentException> {
            Money.of(BigDecimal("-1.00"))
        }
    }

    @Test
    fun `isZero should return true for zero amount`() {
        assert(Money.ZERO.isZero)
        assert(Money.of("0.00").isZero)
    }

    @Test
    fun `isZero should return false for non-zero amount`() {
        assert(!Money.of("0.01").isZero)
    }

    @Test
    fun `should subtract smaller value and return positive result`() {
        val result = Money.of("80.00") - Money.of("30.00")
        assertEquals(Money.of("50.00"), result)
    }

    @Test
    fun `should create money from string via factory`() {
        val money = Money.of("42.50")
        assertEquals(0, money.amount.compareTo(java.math.BigDecimal("42.50")))
    }

    @Test
    fun `toString should format with two decimal places`() {
        assertEquals("1.50", Money.of("1.5").toString())
        assertEquals("100.00", Money.of("100").toString())
    }
}
