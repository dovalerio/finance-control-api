package com.heitor.finance.infrastructure.persistence.adapter

import com.heitor.finance.application.port.output.EntryOutputPort
import com.heitor.finance.domain.exception.CategoryNotFoundException
import com.heitor.finance.domain.exception.SubcategoryNotFoundException
import com.heitor.finance.domain.model.Entry
import com.heitor.finance.domain.valueobject.Period
import com.heitor.finance.infrastructure.persistence.mapper.EntryMapper
import com.heitor.finance.infrastructure.persistence.repository.CategoryJpaRepository
import com.heitor.finance.infrastructure.persistence.repository.EntryJpaRepository
import com.heitor.finance.infrastructure.persistence.repository.SubcategoryJpaRepository
import org.springframework.stereotype.Component

@Component
class EntryRepositoryAdapter(
    private val entryJpaRepository: EntryJpaRepository,
    private val categoryJpaRepository: CategoryJpaRepository,
    private val subcategoryJpaRepository: SubcategoryJpaRepository
) : EntryOutputPort {

    override fun findAll(): List<Entry> =
        entryJpaRepository.findAll().map(EntryMapper::toDomain)

    override fun findById(id: Long): Entry? =
        entryJpaRepository.findById(id).map(EntryMapper::toDomain).orElse(null)

    override fun findByPeriodAndCategoryId(period: Period, categoryId: Long): List<Entry> =
        entryJpaRepository
            .findByPeriodAndCategoryId(period.startDate, period.endDate, categoryId)
            .map(EntryMapper::toDomain)

    override fun save(entry: Entry): Entry {
        val category = categoryJpaRepository.findById(entry.categoryId)
            .orElseThrow { CategoryNotFoundException(entry.categoryId) }

        val subcategory = entry.subcategoryId?.let { subcategoryId ->
            subcategoryJpaRepository.findById(subcategoryId)
                .orElseThrow { SubcategoryNotFoundException(subcategoryId) }
        }

        val entity = EntryMapper.toEntity(entry, category, subcategory)
        return EntryMapper.toDomain(entryJpaRepository.save(entity))
    }

    override fun deleteById(id: Long) = entryJpaRepository.deleteById(id)
}
