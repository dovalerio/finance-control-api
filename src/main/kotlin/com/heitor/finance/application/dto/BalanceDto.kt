package com.heitor.finance.application.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import java.math.BigDecimal

data class BalanceResponse(
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("categoria")
    val category: CategoryResponse?,
    @JsonSerialize(using = BigDecimalToStringSerializer::class)
    @JsonProperty("receita") val revenue: BigDecimal,
    @JsonSerialize(using = BigDecimalToStringSerializer::class)
    @JsonProperty("despesa") val expense: BigDecimal,
    @JsonSerialize(using = BigDecimalToStringSerializer::class)
    @JsonProperty("saldo") val balance: BigDecimal
)
