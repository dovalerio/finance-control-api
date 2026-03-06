package com.heitor.finance.application.dto

import java.math.BigDecimal

data class BalanceResponse(
    val category: CategoryResponse?,
    val revenue: BigDecimal,
    val expense: BigDecimal,
    val balance: BigDecimal
)
