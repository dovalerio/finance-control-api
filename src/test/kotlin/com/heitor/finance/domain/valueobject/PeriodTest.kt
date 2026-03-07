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

    @Test
    fun `contains should return false for date before start`() {
        val period = Period(LocalDate.of(2025, 1, 10), LocalDate.of(2025, 1, 20))
        assert(!period.contains(LocalDate.of(2025, 1, 9)))
    }

    @Test
    fun `contains should return false for date after end`() {
        val period = Period(LocalDate.of(2025, 1, 10), LocalDate.of(2025, 1, 20))
        assert(!period.contains(LocalDate.of(2025, 1, 21)))
    }

    @Test
    fun `contains should return true for boundary dates`() {
        val period = Period(LocalDate.of(2025, 1, 10), LocalDate.of(2025, 1, 20))
        assert(period.contains(LocalDate.of(2025, 1, 10)))
        assert(period.contains(LocalDate.of(2025, 1, 20)))
    }

    @Test
    fun `should allow same start and end date`() {
        val period = Period(LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 1))
        assert(period.contains(LocalDate.of(2025, 6, 1)))
    }
}
