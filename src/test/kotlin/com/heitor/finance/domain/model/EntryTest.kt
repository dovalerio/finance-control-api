package com.heitor.finance.domain.model

import com.heitor.finance.domain.exception.InvalidEntryAmountException
import com.heitor.finance.domain.valueobject.Money
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

class EntryTest {

    private val date = LocalDate.of(2024, 1, 15)

    @Test
    fun `should create EXPENSE entry with valid data`() {
        val entry = Entry(
            comment ="Bus ticket",
            amount = Money.of("10.00"),
            type = EntryType.EXPENSE,
            date = date,
            categoryId = 1L
        )
        assertEquals("Bus ticket", entry.comment)
        assertEquals(Money.of("10.00"), entry.amount)
        assertEquals(EntryType.EXPENSE, entry.type)
        assertEquals(date, entry.date)
        assertEquals(1L, entry.categoryId)
    }

    @Test
    fun `should create INCOME entry with valid data`() {
        val entry = Entry(
            comment ="Salary",
            amount = Money.of("5000.00"),
            type = EntryType.INCOME,
            date = date,
            categoryId = 2L
        )
        assertEquals(EntryType.INCOME, entry.type)
        assertEquals(Money.of("5000.00"), entry.amount)
    }

    @Test
    fun `should allow blank comment`() {
        val entry = Entry(
            comment ="",
            amount = Money.of("10.00"),
            type = EntryType.EXPENSE,
            date = date,
            categoryId = 1L
        )
        assertEquals("", entry.comment)
    }

    @Test
    fun `should allow null subcategoryId`() {
        val entry = Entry(
            comment ="Misc",
            amount = Money.of("10.00"),
            type = EntryType.EXPENSE,
            date = date,
            categoryId = 1L,
            subcategoryId = null
        )
        assertNull(entry.subcategoryId)
    }

    @Test
    fun `should store non-null subcategoryId`() {
        val entry = Entry(
            comment ="Fuel",
            amount = Money.of("50.00"),
            type = EntryType.EXPENSE,
            date = date,
            categoryId = 1L,
            subcategoryId = 7L
        )
        assertNotNull(entry.subcategoryId)
        assertEquals(7L, entry.subcategoryId)
    }

    @Test
    fun `should default id to null`() {
        val entry = Entry(
            comment ="Test",
            amount = Money.of("1.00"),
            type = EntryType.INCOME,
            date = date,
            categoryId = 1L
        )
        assertNull(entry.id)
    }

    @Test
    fun `should throw when amount is zero`() {
        assertThrows<InvalidEntryAmountException> {
            Entry(
                comment ="Bus ticket",
                amount = Money.of("0.00"),
                type = EntryType.EXPENSE,
                date = date,
                categoryId = 1L
            )
        }
    }

    @Test
    fun `should throw when amount is zero using BigDecimal zero`() {
        assertThrows<InvalidEntryAmountException> {
            Entry(
                comment ="Bus ticket",
                amount = Money.ZERO,
                type = EntryType.INCOME,
                date = date,
                categoryId = 1L
            )
        }
    }
}
