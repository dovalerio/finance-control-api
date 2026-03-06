package com.heitor.finance.domain.valueobject

import java.time.LocalDate

data class Period(
    val startDate: LocalDate,
    val endDate: LocalDate
) {
    init {
        require(!startDate.isAfter(endDate)) { "Start date must not be after end date" }
    }

    fun contains(date: LocalDate): Boolean = !date.isBefore(startDate) && !date.isAfter(endDate)
}
