package com.heitor.finance.application.port.input

import com.heitor.finance.application.dto.BalanceResponse
import java.time.LocalDate

interface FindBalanceUseCase {
    fun findByPeriodAndCategory(startDate: LocalDate, endDate: LocalDate, categoryId: Long): BalanceResponse
}
