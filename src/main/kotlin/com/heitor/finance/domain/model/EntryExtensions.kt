package com.heitor.finance.domain.model

import java.math.BigDecimal

fun Entry.signedValue(): BigDecimal =
    if (type == EntryType.INCOME) amount.amount
    else amount.amount.negate()
