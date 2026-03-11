package com.heitor.finance.infrastructure.config

import com.heitor.finance.application.port.input.DeleteCategoryUseCase
import com.heitor.finance.application.port.output.CategoryOutputPort
import com.heitor.finance.application.port.output.EntryOutputPort
import com.heitor.finance.application.port.output.SubcategoryOutputPort
import com.heitor.finance.application.usecase.FindCategoryUseCaseImpl
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
class UseCaseConfig(
    private val categoryPort: CategoryOutputPort,
    private val subcategoryPort: SubcategoryOutputPort,
    private val entryPort: EntryOutputPort
) {

    private val balanceCalculator = BalanceCalculatorService()

    @Bean fun createCategoryUseCase() = CreateCategoryUseCaseImpl(categoryPort)
    @Bean fun findCategoryUseCase() = FindCategoryUseCaseImpl(categoryPort)
    @Bean fun updateCategoryUseCase() = UpdateCategoryUseCaseImpl(categoryPort)
    @Bean fun deleteCategoryUseCase(): DeleteCategoryUseCase = DeleteCategoryUseCaseImpl(categoryPort)

    @Bean fun findBalanceUseCase() = FindBalanceUseCaseImpl(categoryPort, entryPort, balanceCalculator)

    @Bean fun createSubcategoryUseCase() = CreateSubcategoryUseCaseImpl(subcategoryPort, categoryPort)
    @Bean fun findSubcategoryUseCase() = FindSubcategoryUseCaseImpl(subcategoryPort)
    @Bean fun updateSubcategoryUseCase() = UpdateSubcategoryUseCaseImpl(subcategoryPort, categoryPort)
    @Bean fun deleteSubcategoryUseCase() = DeleteSubcategoryUseCaseImpl(subcategoryPort, entryPort)

    @Bean fun createEntryUseCase() = CreateEntryUseCaseImpl(entryPort, subcategoryPort)
    @Bean fun findEntryUseCase() = FindEntryUseCaseImpl(entryPort)
    @Bean fun updateEntryUseCase() = UpdateEntryUseCaseImpl(entryPort, subcategoryPort)
    @Bean fun deleteEntryUseCase() = DeleteEntryUseCaseImpl(entryPort)
}
