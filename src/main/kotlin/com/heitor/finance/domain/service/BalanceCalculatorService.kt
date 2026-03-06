package com.heitor.finance.domain.service

import com.heitor.finance.domain.model.Balance
import com.heitor.finance.domain.model.Category
import com.heitor.finance.domain.model.Entry
import com.heitor.finance.domain.model.EntryType
import com.heitor.finance.domain.valueobject.Money

class BalanceCalculatorService {

    fun calculate(category: Category, entries: List<Entry>): Balance {
        val income = entries
            .filter { it.type == EntryType.INCOME }
            .fold(Money.ZERO) { acc, entry -> acc + entry.amount }

        val expense = entries
            .filter { it.type == EntryType.EXPENSE }
            .fold(Money.ZERO) { acc, entry -> acc + entry.amount }

        return Balance(
            category = category,
            income = income,
            expense = expense
        )
    }
}
