package com.heitor.finance.domain.model

import com.heitor.finance.domain.valueobject.Money
import java.math.BigDecimal

data class Balance(
    val category: Category,
    val income: Money,
    val expense: Money,
    val net: BigDecimal = income.amount.subtract(expense.amount)
)
