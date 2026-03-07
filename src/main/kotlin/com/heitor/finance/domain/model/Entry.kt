package com.heitor.finance.domain.model

import com.heitor.finance.domain.valueobject.Money
import java.time.LocalDate

data class Entry(
    val id: Long? = null,
    val description: String,
    val amount: Money,
    val type: EntryType,
    val date: LocalDate,
    val categoryId: Long,
    val subcategoryId: Long? = null
) {
    init {
        require(!amount.isZero) { "Entry amount must not be zero" }
    }
}
