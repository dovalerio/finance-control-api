package com.heitor.finance.application.usecase

import com.heitor.finance.application.dto.BalanceResponse
import com.heitor.finance.application.dto.CategoryResponse
import com.heitor.finance.application.port.input.FindBalanceUseCase
import com.heitor.finance.application.port.output.CategoryOutputPort
import com.heitor.finance.application.port.output.EntryOutputPort
import com.heitor.finance.application.util.orThrow
import com.heitor.finance.domain.exception.CategoryNotFoundException
import com.heitor.finance.domain.exception.InvalidPeriodException
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
        categoryId: Long?
    ): BalanceResponse {
        val period = try {
            Period(startDate, endDate)
        } catch (ex: IllegalArgumentException) {
            throw InvalidPeriodException(ex.message ?: "Invalid period")
        }

        if (categoryId == null) {
            val entries = entryOutputPort.findByPeriod(period)
            val (revenue, expense) = balanceCalculatorService.calculate(entries)
            return BalanceResponse(
                category = null,
                revenue = revenue.amount,
                expense = expense.amount,
                balance = revenue.amount.subtract(expense.amount)
            )
        }

        val category = categoryOutputPort.findById(categoryId)
            .orThrow { CategoryNotFoundException(categoryId) }
        val entries = entryOutputPort.findByPeriodAndCategoryId(period, categoryId)
        val balance = balanceCalculatorService.calculate(category, entries)

        return BalanceResponse(
            category = CategoryResponse(id = category.id!!, name = category.name),
            revenue = balance.income.amount,
            expense = balance.expense.amount,
            balance = balance.net
        )
    }
}
