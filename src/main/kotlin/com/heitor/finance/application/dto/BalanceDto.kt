package com.heitor.finance.application.dto

import java.math.BigDecimal

data class BalanceResponse(
    val category: CategoryResponse,
    val income: BigDecimal,
    val expense: BigDecimal,
    val net: BigDecimal
)
