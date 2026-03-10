package com.heitor.finance.domain.exception

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DomainExceptionTest {

    @Test
    fun `CategoryNotFoundException should include id in message`() {
        val ex = CategoryNotFoundException(42L)
        assertEquals("Category not found with id=42", ex.message)
        assertThat(ex).isInstanceOf(DomainException::class.java)
    }

    @Test
    fun `SubcategoryNotFoundException should include id in message`() {
        val ex = SubcategoryNotFoundException(7L)
        assertEquals("Subcategory not found with id=7", ex.message)
        assertThat(ex).isInstanceOf(DomainException::class.java)
    }

    @Test
    fun `EntryNotFoundException should include id in message`() {
        val ex = EntryNotFoundException(99L)
        assertEquals("Entry not found with id=99", ex.message)
        assertThat(ex).isInstanceOf(DomainException::class.java)
    }

    @Test
    fun `InvalidPeriodException should preserve custom message`() {
        val ex = InvalidPeriodException("Start date must not be after end date")
        assertEquals("Start date must not be after end date", ex.message)
        assertThat(ex).isInstanceOf(DomainException::class.java)
    }

    @Test
    fun `CategoryAlreadyExistsException should include name in message`() {
        val ex = CategoryAlreadyExistsException("Transport")
        assertEquals("Category already exists with name='Transport'", ex.message)
        assertThat(ex).isInstanceOf(DomainException::class.java)
    }

    @Test
    fun `SubcategoryAlreadyExistsException should include name and categoryId in message`() {
        val ex = SubcategoryAlreadyExistsException("Fuel", 3L)
        assertEquals("Subcategory already exists with name='Fuel' in category id=3", ex.message)
        assertThat(ex).isInstanceOf(DomainException::class.java)
    }
}
