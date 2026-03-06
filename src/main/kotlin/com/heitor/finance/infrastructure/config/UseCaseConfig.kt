package com.heitor.finance.infrastructure.config

import com.heitor.finance.application.port.input.DeleteCategoryUseCase
import com.heitor.finance.application.port.output.CategoryOutputPort
import com.heitor.finance.application.port.output.EntryOutputPort
import com.heitor.finance.application.service.CategoryApplicationService
import com.heitor.finance.application.usecase.CreateCategoryUseCaseImpl
import com.heitor.finance.application.usecase.DeleteCategoryUseCaseImpl
import com.heitor.finance.application.usecase.FindBalanceUseCaseImpl
import com.heitor.finance.application.usecase.UpdateCategoryUseCaseImpl
import com.heitor.finance.domain.service.BalanceCalculatorService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UseCaseConfig {

    @Bean
    fun balanceCalculatorService(): BalanceCalculatorService = BalanceCalculatorService()

    @Bean
    fun createCategoryUseCase(categoryOutputPort: CategoryOutputPort) =
        CreateCategoryUseCaseImpl(categoryOutputPort)

    @Bean
    fun findCategoryUseCase(categoryOutputPort: CategoryOutputPort) =
        CategoryApplicationService(categoryOutputPort)

    @Bean
    fun updateCategoryUseCase(categoryOutputPort: CategoryOutputPort) =
        UpdateCategoryUseCaseImpl(categoryOutputPort)

    @Bean
    fun deleteCategoryUseCase(categoryOutputPort: CategoryOutputPort): DeleteCategoryUseCase =
        DeleteCategoryUseCaseImpl(categoryOutputPort)

    @Bean
    fun findBalanceUseCase(
        categoryOutputPort: CategoryOutputPort,
        entryOutputPort: EntryOutputPort,
        balanceCalculatorService: BalanceCalculatorService
    ) = FindBalanceUseCaseImpl(categoryOutputPort, entryOutputPort, balanceCalculatorService)
}
