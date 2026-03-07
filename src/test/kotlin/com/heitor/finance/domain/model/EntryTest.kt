package com.heitor.finance.domain.model

import com.heitor.finance.domain.valueobject.Money
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

class EntryTest {

    private val date = LocalDate.of(2024, 1, 15)

    @Test
    fun `should create entry with valid data`() {
        val entry = Entry(
            description = "Bus ticket",
            amount = Money.of("10.00"),
            type = EntryType.EXPENSE,
            date = date,
            categoryId = 1L
        )
        assertEquals("Bus ticket", entry.description)
        assertEquals(Money.of("10.00"), entry.amount)
    }

    @Test
    fun `should throw when description is blank`() {
        assertThrows<IllegalArgumentException> {
            Entry(
                description = "",
                amount = Money.of("10.00"),
                type = EntryType.EXPENSE,
                date = date,
                categoryId = 1L
            )
        }
    }

    @Test
    fun `should throw when description is whitespace only`() {
        assertThrows<IllegalArgumentException> {
            Entry(
                description = "   ",
                amount = Money.of("10.00"),
                type = EntryType.EXPENSE,
                date = date,
                categoryId = 1L
            )
        }
    }

    @Test
    fun `should throw when amount is zero`() {
        assertThrows<IllegalArgumentException> {
            Entry(
                description = "Bus ticket",
                amount = Money.of("0.00"),
                type = EntryType.EXPENSE,
                date = date,
                categoryId = 1L
            )
        }
    }

    @Test
    fun `should throw when amount is zero using BigDecimal zero`() {
        assertThrows<IllegalArgumentException> {
            Entry(
                description = "Bus ticket",
                amount = Money.ZERO,
                type = EntryType.INCOME,
                date = date,
                categoryId = 1L
            )
        }
    }
}
