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
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UseCaseConfigTest {

    private val categoryOutputPort = mockk<CategoryOutputPort>()
    private val subcategoryOutputPort = mockk<SubcategoryOutputPort>()
    private val entryOutputPort = mockk<EntryOutputPort>()
    private val config = UseCaseConfig()

    @Test
    fun `balanceCalculatorService should return BalanceCalculatorService instance`() {
        assertThat(config.balanceCalculatorService()).isInstanceOf(BalanceCalculatorService::class.java)
    }

    @Test
    fun `createCategoryUseCase should return CreateCategoryUseCaseImpl`() {
        assertThat(config.createCategoryUseCase(categoryOutputPort)).isInstanceOf(CreateCategoryUseCaseImpl::class.java)
    }

    @Test
    fun `findCategoryUseCase should return FindCategoryUseCaseImpl`() {
        assertThat(config.findCategoryUseCase(categoryOutputPort)).isInstanceOf(FindCategoryUseCaseImpl::class.java)
    }

    @Test
    fun `updateCategoryUseCase should return UpdateCategoryUseCaseImpl`() {
        assertThat(config.updateCategoryUseCase(categoryOutputPort)).isInstanceOf(UpdateCategoryUseCaseImpl::class.java)
    }

    @Test
    fun `deleteCategoryUseCase should return DeleteCategoryUseCaseImpl`() {
        assertThat(config.deleteCategoryUseCase(categoryOutputPort)).isInstanceOf(DeleteCategoryUseCaseImpl::class.java)
    }

    @Test
    fun `findBalanceUseCase should return FindBalanceUseCaseImpl`() {
        val balanceCalculatorService = config.balanceCalculatorService()
        assertThat(config.findBalanceUseCase(categoryOutputPort, entryOutputPort, balanceCalculatorService))
            .isInstanceOf(FindBalanceUseCaseImpl::class.java)
    }

    @Test
    fun `createSubcategoryUseCase should return CreateSubcategoryUseCaseImpl`() {
        assertThat(config.createSubcategoryUseCase(subcategoryOutputPort, categoryOutputPort))
            .isInstanceOf(CreateSubcategoryUseCaseImpl::class.java)
    }

    @Test
    fun `createEntryUseCase should return CreateEntryUseCaseImpl`() {
        assertThat(config.createEntryUseCase(entryOutputPort, subcategoryOutputPort))
            .isInstanceOf(CreateEntryUseCaseImpl::class.java)
    }

    @Test
    fun `findSubcategoryUseCase should return FindSubcategoryUseCaseImpl`() {
        assertThat(config.findSubcategoryUseCase(subcategoryOutputPort)).isInstanceOf(FindSubcategoryUseCaseImpl::class.java)
    }

    @Test
    fun `updateSubcategoryUseCase should return UpdateSubcategoryUseCaseImpl`() {
        assertThat(config.updateSubcategoryUseCase(subcategoryOutputPort, categoryOutputPort))
            .isInstanceOf(UpdateSubcategoryUseCaseImpl::class.java)
    }

    @Test
    fun `deleteSubcategoryUseCase should return DeleteSubcategoryUseCaseImpl`() {
        assertThat(config.deleteSubcategoryUseCase(subcategoryOutputPort, entryOutputPort))
            .isInstanceOf(DeleteSubcategoryUseCaseImpl::class.java)
    }

    @Test
    fun `findEntryUseCase should return FindEntryUseCaseImpl`() {
        assertThat(config.findEntryUseCase(entryOutputPort)).isInstanceOf(FindEntryUseCaseImpl::class.java)
    }

    @Test
    fun `updateEntryUseCase should return UpdateEntryUseCaseImpl`() {
        assertThat(config.updateEntryUseCase(entryOutputPort, subcategoryOutputPort))
            .isInstanceOf(UpdateEntryUseCaseImpl::class.java)
    }

    @Test
    fun `deleteEntryUseCase should return DeleteEntryUseCaseImpl`() {
        assertThat(config.deleteEntryUseCase(entryOutputPort)).isInstanceOf(DeleteEntryUseCaseImpl::class.java)
    }
}
