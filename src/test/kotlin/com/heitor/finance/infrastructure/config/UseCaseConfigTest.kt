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
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UseCaseConfigTest {

    private val config = UseCaseConfig(
        categoryPort = mockk<CategoryOutputPort>(),
        subcategoryPort = mockk<SubcategoryOutputPort>(),
        entryPort = mockk<EntryOutputPort>()
    )

    @Test
    fun `createCategoryUseCase should return CreateCategoryUseCaseImpl`() {
        assertThat(config.createCategoryUseCase()).isInstanceOf(CreateCategoryUseCaseImpl::class.java)
    }

    @Test
    fun `findCategoryUseCase should return FindCategoryUseCaseImpl`() {
        assertThat(config.findCategoryUseCase()).isInstanceOf(FindCategoryUseCaseImpl::class.java)
    }

    @Test
    fun `updateCategoryUseCase should return UpdateCategoryUseCaseImpl`() {
        assertThat(config.updateCategoryUseCase()).isInstanceOf(UpdateCategoryUseCaseImpl::class.java)
    }

    @Test
    fun `deleteCategoryUseCase should return DeleteCategoryUseCaseImpl`() {
        assertThat(config.deleteCategoryUseCase()).isInstanceOf(DeleteCategoryUseCaseImpl::class.java)
    }

    @Test
    fun `findBalanceUseCase should return FindBalanceUseCaseImpl`() {
        assertThat(config.findBalanceUseCase()).isInstanceOf(FindBalanceUseCaseImpl::class.java)
    }

    @Test
    fun `createSubcategoryUseCase should return CreateSubcategoryUseCaseImpl`() {
        assertThat(config.createSubcategoryUseCase()).isInstanceOf(CreateSubcategoryUseCaseImpl::class.java)
    }

    @Test
    fun `createEntryUseCase should return CreateEntryUseCaseImpl`() {
        assertThat(config.createEntryUseCase()).isInstanceOf(CreateEntryUseCaseImpl::class.java)
    }

    @Test
    fun `findSubcategoryUseCase should return FindSubcategoryUseCaseImpl`() {
        assertThat(config.findSubcategoryUseCase()).isInstanceOf(FindSubcategoryUseCaseImpl::class.java)
    }

    @Test
    fun `updateSubcategoryUseCase should return UpdateSubcategoryUseCaseImpl`() {
        assertThat(config.updateSubcategoryUseCase()).isInstanceOf(UpdateSubcategoryUseCaseImpl::class.java)
    }

    @Test
    fun `deleteSubcategoryUseCase should return DeleteSubcategoryUseCaseImpl`() {
        assertThat(config.deleteSubcategoryUseCase()).isInstanceOf(DeleteSubcategoryUseCaseImpl::class.java)
    }

    @Test
    fun `findEntryUseCase should return FindEntryUseCaseImpl`() {
        assertThat(config.findEntryUseCase()).isInstanceOf(FindEntryUseCaseImpl::class.java)
    }

    @Test
    fun `updateEntryUseCase should return UpdateEntryUseCaseImpl`() {
        assertThat(config.updateEntryUseCase()).isInstanceOf(UpdateEntryUseCaseImpl::class.java)
    }

    @Test
    fun `deleteEntryUseCase should return DeleteEntryUseCaseImpl`() {
        assertThat(config.deleteEntryUseCase()).isInstanceOf(DeleteEntryUseCaseImpl::class.java)
    }
}
