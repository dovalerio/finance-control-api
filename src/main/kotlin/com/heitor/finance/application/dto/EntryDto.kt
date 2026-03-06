package com.heitor.finance.application.dto

import com.heitor.finance.domain.model.EntryType
import java.math.BigDecimal
import java.time.LocalDate

data class CreateEntryRequest(
    val description: String,
    val amount: BigDecimal,
    val type: EntryType,
    val date: LocalDate,
    val categoryId: Long,
    val subcategoryId: Long? = null
)

data class EntryResponse(
    val id: Long,
    val description: String,
    val amount: BigDecimal,
    val type: EntryType,
    val date: LocalDate,
    val categoryId: Long,
    val subcategoryId: Long?
)
