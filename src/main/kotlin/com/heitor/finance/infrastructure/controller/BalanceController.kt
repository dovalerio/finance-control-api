package com.heitor.finance.infrastructure.controller

import com.heitor.finance.application.dto.BalanceResponse
import com.heitor.finance.application.port.input.FindBalanceUseCase
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/v1/balance")
class BalanceController(
    private val findBalanceUseCase: FindBalanceUseCase
) {

    @GetMapping
    fun findBalance(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate,
        @RequestParam(required = false) categoryId: Long?
    ): ResponseEntity<BalanceResponse> =
        ResponseEntity.ok(findBalanceUseCase.findByPeriodAndCategory(startDate, endDate, categoryId))
}
