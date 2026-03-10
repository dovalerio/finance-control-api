package com.heitor.finance.infrastructure.config

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
        assert(useCase is FindCategoryUseCaseImpl)
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

    @Test
    fun `findSubcategoryUseCase should return FindSubcategoryUseCaseImpl`() {
        val useCase = config.findSubcategoryUseCase(subcategoryOutputPort)

        assertNotNull(useCase)
        assert(useCase is FindSubcategoryUseCaseImpl)
    }

    @Test
    fun `updateSubcategoryUseCase should return UpdateSubcategoryUseCaseImpl`() {
        val useCase = config.updateSubcategoryUseCase(subcategoryOutputPort, categoryOutputPort)

        assertNotNull(useCase)
        assert(useCase is UpdateSubcategoryUseCaseImpl)
    }

    @Test
    fun `deleteSubcategoryUseCase should return DeleteSubcategoryUseCaseImpl`() {
        val useCase = config.deleteSubcategoryUseCase(subcategoryOutputPort, entryOutputPort)

        assertNotNull(useCase)
        assert(useCase is DeleteSubcategoryUseCaseImpl)
    }

    @Test
    fun `findEntryUseCase should return FindEntryUseCaseImpl`() {
        val useCase = config.findEntryUseCase(entryOutputPort)

        assertNotNull(useCase)
        assert(useCase is FindEntryUseCaseImpl)
    }

    @Test
    fun `updateEntryUseCase should return UpdateEntryUseCaseImpl`() {
        val useCase = config.updateEntryUseCase(entryOutputPort, subcategoryOutputPort)

        assertNotNull(useCase)
        assert(useCase is UpdateEntryUseCaseImpl)
    }

    @Test
    fun `deleteEntryUseCase should return DeleteEntryUseCaseImpl`() {
        val useCase = config.deleteEntryUseCase(entryOutputPort)

        assertNotNull(useCase)
        assert(useCase is DeleteEntryUseCaseImpl)
    }
}
