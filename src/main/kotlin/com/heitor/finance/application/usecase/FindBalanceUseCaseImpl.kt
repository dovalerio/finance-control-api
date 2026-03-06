package com.heitor.finance.application.usecase

import com.heitor.finance.application.dto.BalanceResponse
import com.heitor.finance.application.dto.CategoryResponse
import com.heitor.finance.application.port.input.FindBalanceUseCase
import com.heitor.finance.application.port.output.CategoryOutputPort
import com.heitor.finance.application.port.output.EntryOutputPort
import com.heitor.finance.domain.exception.CategoryNotFoundException
import com.heitor.finance.domain.service.BalanceCalculatorService
import com.heitor.finance.domain.valueobject.Period
import java.time.LocalDate

class FindBalanceUseCaseImpl(
    private val categoryOutputPort: CategoryOutputPort,
    private val entryOutputPort: EntryOutputPort,
    private val balanceCalculatorService: BalanceCalculatorService
) : FindBalanceUseCase {

    override fun findByPeriodAndCategory(
        startDate: LocalDate,
        endDate: LocalDate,
        categoryId: Long
    ): BalanceResponse {
        val category = categoryOutputPort.findById(categoryId)
            ?: throw CategoryNotFoundException(categoryId)

        val period = Period(startDate, endDate)
        val entries = entryOutputPort.findByPeriodAndCategoryId(period, categoryId)
        val balance = balanceCalculatorService.calculate(category, entries)

        return BalanceResponse(
            category = CategoryResponse(id = category.id!!, name = category.name),
            income = balance.income.amount,
            expense = balance.expense.amount,
            net = balance.net.amount
        )
    }
}
