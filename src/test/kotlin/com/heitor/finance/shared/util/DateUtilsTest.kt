package com.heitor.finance.shared.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

class DateUtilsTest {

    @Test
    fun `parse should convert yyyy-MM-dd string to LocalDate`() {
        val result = DateUtils.parse("2024-01-15")

        assertEquals(LocalDate.of(2024, 1, 15), result)
    }

    @Test
    fun `parse should handle first day of year`() {
        val result = DateUtils.parse("2024-01-01")

        assertEquals(LocalDate.of(2024, 1, 1), result)
    }

    @Test
    fun `parse should handle last day of year`() {
        val result = DateUtils.parse("2024-12-31")

        assertEquals(LocalDate.of(2024, 12, 31), result)
    }

    @Test
    fun `format should convert LocalDate to yyyy-MM-dd string`() {
        val date = LocalDate.of(2024, 6, 5)

        val result = DateUtils.format(date)

        assertEquals("2024-06-05", result)
    }

    @Test
    fun `format should pad month and day with zeros`() {
        val date = LocalDate.of(2023, 3, 7)

        val result = DateUtils.format(date)

        assertEquals("2023-03-07", result)
    }

    @Test
    fun `parse and format should be inverse operations`() {
        val original = "2024-08-20"

        val parsed = DateUtils.parse(original)
        val formatted = DateUtils.format(parsed)

        assertEquals(original, formatted)
    }

    @Test
    fun `parse should throw exception for invalid date format`() {
        assertThrows<Exception> {
            DateUtils.parse("15/01/2024")
        }
    }
}
