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
}
