package com.heitor.finance.application.dto

import java.math.BigDecimal
import java.time.LocalDate

/**
 * Alinhado ao contrato api.yml.
 * value positivo → INCOME (receita)
 * value negativo → EXPENSE (despesa)
 */
data class CreateEntryRequest(
    val value: BigDecimal,
    val subcategoryId: Long,
    val date: LocalDate = LocalDate.now(),
    val comment: String? = null
)

data class EntryResponse(
    val id: Long,
    val value: BigDecimal,
    val date: LocalDate,
    val subcategoryId: Long?,
    val comment: String?
)
