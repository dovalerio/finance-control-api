package com.heitor.finance.infrastructure.config

import com.heitor.finance.application.port.input.DeleteCategoryUseCase
import com.heitor.finance.application.port.output.CategoryOutputPort
import com.heitor.finance.application.port.output.EntryOutputPort
import com.heitor.finance.application.port.output.SubcategoryOutputPort
import com.heitor.finance.application.service.CategoryApplicationService
import com.heitor.finance.application.usecase.CreateCategoryUseCaseImpl
import com.heitor.finance.application.usecase.CreateEntryUseCaseImpl
import com.heitor.finance.application.usecase.CreateSubcategoryUseCaseImpl
import com.heitor.finance.application.usecase.DeleteCategoryUseCaseImpl
import com.heitor.finance.application.usecase.DeleteEntryUseCaseImpl
import com.heitor.finance.application.usecase.DeleteSubcategoryUseCaseImpl
import com.heitor.finance.application.usecase.FindBalanceUseCaseImpl
import com.heitor.finance.application.usecase.FindEntryUseCaseImpl
import com.heitor.finance.application.usecase.FindSubcategoryUseCaseImpl
import com.heitor.finance.application.usecase.UpdateCategoryUseCaseImpl
import com.heitor.finance.application.usecase.UpdateEntryUseCaseImpl
import com.heitor.finance.application.usecase.UpdateSubcategoryUseCaseImpl
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

    @Bean
    fun createSubcategoryUseCase(
        subcategoryOutputPort: SubcategoryOutputPort,
        categoryOutputPort: CategoryOutputPort
    ) = CreateSubcategoryUseCaseImpl(subcategoryOutputPort, categoryOutputPort)

    @Bean
    fun findSubcategoryUseCase(subcategoryOutputPort: SubcategoryOutputPort) =
        FindSubcategoryUseCaseImpl(subcategoryOutputPort)

    @Bean
    fun updateSubcategoryUseCase(
        subcategoryOutputPort: SubcategoryOutputPort,
        categoryOutputPort: CategoryOutputPort
    ) = UpdateSubcategoryUseCaseImpl(subcategoryOutputPort, categoryOutputPort)

    @Bean
    fun deleteSubcategoryUseCase(
        subcategoryOutputPort: SubcategoryOutputPort,
        entryOutputPort: EntryOutputPort
    ) = DeleteSubcategoryUseCaseImpl(subcategoryOutputPort, entryOutputPort)

    @Bean
    fun createEntryUseCase(
        entryOutputPort: EntryOutputPort,
        subcategoryOutputPort: SubcategoryOutputPort
    ) = CreateEntryUseCaseImpl(entryOutputPort, subcategoryOutputPort)

    @Bean
    fun findEntryUseCase(entryOutputPort: EntryOutputPort) =
        FindEntryUseCaseImpl(entryOutputPort)

    @Bean
    fun updateEntryUseCase(
        entryOutputPort: EntryOutputPort,
        subcategoryOutputPort: SubcategoryOutputPort
    ) = UpdateEntryUseCaseImpl(entryOutputPort, subcategoryOutputPort)

    @Bean
    fun deleteEntryUseCase(entryOutputPort: EntryOutputPort) =
        DeleteEntryUseCaseImpl(entryOutputPort)
}
