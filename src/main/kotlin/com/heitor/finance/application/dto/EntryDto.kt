package com.heitor.finance.application.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.heitor.finance.domain.model.Entry
import com.heitor.finance.domain.model.signedValue
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDate

/**
 * Alinhado ao contrato api.yml.
 * valor positivo → INCOME (receita)
 * valor negativo → EXPENSE (despesa)
 */
data class CreateEntryRequest(
    @field:NotNull(message = "O campo 'valor' é obrigatório")
    @JsonProperty("valor") val value: BigDecimal? = null,
    @field:NotNull(message = "O campo 'id_subcategoria' é obrigatório")
    @JsonProperty("id_subcategoria") val subcategoryId: Long? = null,
    @JsonFormat(pattern = "dd/MM/yyyy")
    @JsonProperty("data") val date: LocalDate? = null,
    @JsonProperty("comentario") val comment: String? = null
)

data class EntryResponse(
    @JsonProperty("id_lancamento") val id: Long,
    @JsonProperty("valor") val value: BigDecimal,
    @JsonFormat(pattern = "dd/MM/yyyy")
    @JsonProperty("data") val date: LocalDate,
    @JsonProperty("id_subcategoria") val subcategoryId: Long?,
    @JsonProperty("comentario") val comment: String
)

fun Entry.toResponse() = EntryResponse(
    id = id!!,
    value = signedValue(),
    date = date,
    subcategoryId = subcategoryId,
    comment = comment.ifBlank { "" }
)
