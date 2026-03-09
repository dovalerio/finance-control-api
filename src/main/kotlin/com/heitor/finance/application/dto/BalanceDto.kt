package com.heitor.finance.application.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class BalanceResponse(
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("categoria")
    val category: CategoryResponse?,
    @JsonProperty("receita") val revenue: BigDecimal,
    @JsonProperty("despesa") val expense: BigDecimal,
    @JsonProperty("saldo") val balance: BigDecimal
)
