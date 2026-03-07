package com.heitor.finance.infrastructure.config

import com.heitor.finance.application.port.output.CategoryOutputPort
import com.heitor.finance.application.port.output.EntryOutputPort
import com.heitor.finance.application.port.output.SubcategoryOutputPort
import com.heitor.finance.application.service.CategoryApplicationService
import com.heitor.finance.application.usecase.CreateCategoryUseCaseImpl
import com.heitor.finance.application.usecase.CreateEntryUseCaseImpl
import com.heitor.finance.application.usecase.CreateSubcategoryUseCaseImpl
import com.heitor.finance.application.usecase.DeleteCategoryUseCaseImpl
import com.heitor.finance.application.usecase.FindBalanceUseCaseImpl
import com.heitor.finance.application.usecase.UpdateCategoryUseCaseImpl
import com.heitor.finance.domain.service.BalanceCalculatorService
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class UseCaseConfigTest {

    private val categoryOutputPort = mockk<CategoryOutputPort>()
    private val subcategoryOutputPort = mockk<SubcategoryOutputPort>()
    private val entryOutputPort = mockk<EntryOutputPort>()
    private val config = UseCaseConfig()

    @Test
    fun `balanceCalculatorService should return BalanceCalculatorService instance`() {
        val service = config.balanceCalculatorService()

        assertNotNull(service)
        assert(service is BalanceCalculatorService)
    }

    @Test
    fun `createCategoryUseCase should return CreateCategoryUseCaseImpl`() {
        val useCase = config.createCategoryUseCase(categoryOutputPort)

        assertNotNull(useCase)
        assert(useCase is CreateCategoryUseCaseImpl)
    }

    @Test
    fun `findCategoryUseCase should return CategoryApplicationService`() {
        val useCase = config.findCategoryUseCase(categoryOutputPort)

        assertNotNull(useCase)
        assert(useCase is CategoryApplicationService)
    }

    @Test
    fun `updateCategoryUseCase should return UpdateCategoryUseCaseImpl`() {
        val useCase = config.updateCategoryUseCase(categoryOutputPort)

        assertNotNull(useCase)
        assert(useCase is UpdateCategoryUseCaseImpl)
    }

    @Test
    fun `deleteCategoryUseCase should return DeleteCategoryUseCaseImpl`() {
        val useCase = config.deleteCategoryUseCase(categoryOutputPort)

        assertNotNull(useCase)
        assert(useCase is DeleteCategoryUseCaseImpl)
    }

    @Test
    fun `findBalanceUseCase should return FindBalanceUseCaseImpl`() {
        val balanceCalculatorService = config.balanceCalculatorService()
        val useCase = config.findBalanceUseCase(categoryOutputPort, entryOutputPort, balanceCalculatorService)

        assertNotNull(useCase)
        assert(useCase is FindBalanceUseCaseImpl)
    }

    @Test
    fun `createSubcategoryUseCase should return CreateSubcategoryUseCaseImpl`() {
        val useCase = config.createSubcategoryUseCase(subcategoryOutputPort, categoryOutputPort)

        assertNotNull(useCase)
        assert(useCase is CreateSubcategoryUseCaseImpl)
    }

    @Test
    fun `createEntryUseCase should return CreateEntryUseCaseImpl`() {
        val useCase = config.createEntryUseCase(entryOutputPort, subcategoryOutputPort)

        assertNotNull(useCase)
        assert(useCase is CreateEntryUseCaseImpl)
    }
}
