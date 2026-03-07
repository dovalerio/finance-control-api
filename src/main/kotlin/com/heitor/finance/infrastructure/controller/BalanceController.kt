package com.heitor.finance.infrastructure.controller

import com.heitor.finance.application.dto.BalanceResponse
import com.heitor.finance.application.port.input.FindBalanceUseCase
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/v1/balanco")
class BalanceController(
    private val findBalanceUseCase: FindBalanceUseCase
) {

    @GetMapping
    fun findBalance(
        @RequestParam("data_inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate,
        @RequestParam("data_fim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate,
        @RequestParam("id_categoria", required = false) categoryId: Long?
    ): ResponseEntity<BalanceResponse> =
        ResponseEntity.ok(findBalanceUseCase.findByPeriodAndCategory(startDate, endDate, categoryId))
}
