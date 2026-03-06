package com.heitor.finance.shared.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DateUtils {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun parse(value: String): LocalDate = LocalDate.parse(value, formatter)
    fun format(date: LocalDate): String = date.format(formatter)
}
