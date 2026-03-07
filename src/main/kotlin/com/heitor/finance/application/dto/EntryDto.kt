package com.heitor.finance.application.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.time.LocalDate

/**
 * Alinhado ao contrato api.yml.
 * valor positivo → INCOME (receita)
 * valor negativo → EXPENSE (despesa)
 */
data class CreateEntryRequest(
    @JsonProperty("valor") val value: BigDecimal,
    @JsonProperty("id_subcategoria") val subcategoryId: Long,
    @JsonProperty("data") val date: LocalDate? = null,
    @JsonProperty("comentario") val comment: String? = null
)

data class EntryResponse(
    @JsonProperty("id_lancamento") val id: Long,
    @JsonProperty("valor") val value: BigDecimal,
    @JsonProperty("data") val date: LocalDate,
    @JsonProperty("id_subcategoria") val subcategoryId: Long?,
    @JsonProperty("comentario") val comment: String?
)
