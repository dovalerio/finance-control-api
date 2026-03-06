package com.heitor.finance.domain.valueobject

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

class PeriodTest {

    @Test
    fun `should create valid period`() {
        val period = Period(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31))
        assert(period.contains(LocalDate.of(2025, 1, 15)))
    }

    @Test
    fun `should throw exception when start date is after end date`() {
        assertThrows<IllegalArgumentException> {
            Period(LocalDate.of(2025, 2, 1), LocalDate.of(2025, 1, 1))
        }
    }
}
