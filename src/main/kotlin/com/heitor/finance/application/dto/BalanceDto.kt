package com.heitor.finance.application.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class BalanceResponse(
    @JsonIgnore val category: CategoryResponse?,
    @JsonProperty("receita") val revenue: BigDecimal,
    @JsonProperty("despesa") val expense: BigDecimal,
    @JsonProperty("saldo") val balance: BigDecimal
)
