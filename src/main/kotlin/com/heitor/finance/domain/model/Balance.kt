package com.heitor.finance.domain.model

import com.heitor.finance.domain.valueobject.Money

data class Balance(
    val category: Category,
    val income: Money,
    val expense: Money,
    val net: Money = income - expense
)
