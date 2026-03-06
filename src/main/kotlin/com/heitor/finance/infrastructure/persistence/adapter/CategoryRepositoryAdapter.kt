package com.heitor.finance.infrastructure.persistence.adapter

import com.heitor.finance.application.port.output.CategoryOutputPort
import com.heitor.finance.domain.model.Category
import com.heitor.finance.infrastructure.persistence.mapper.CategoryMapper
import com.heitor.finance.infrastructure.persistence.repository.CategoryJpaRepository
import org.springframework.stereotype.Component

@Component
class CategoryRepositoryAdapter(
    private val categoryJpaRepository: CategoryJpaRepository
) : CategoryOutputPort {

    override fun findAll(): List<Category> =
        categoryJpaRepository.findAllWithSubcategories().map(CategoryMapper::toDomain)

    override fun findById(id: Long): Category? =
        categoryJpaRepository.findByIdWithSubcategories(id).map(CategoryMapper::toDomain).orElse(null)

    override fun save(category: Category): Category {
        val entity = CategoryMapper.toEntity(category)
        return CategoryMapper.toDomain(categoryJpaRepository.save(entity))
    }

    override fun update(category: Category): Category {
        val entity = CategoryMapper.toEntity(category)
        return CategoryMapper.toDomain(categoryJpaRepository.save(entity))
    }

    override fun deleteById(id: Long) = categoryJpaRepository.deleteById(id)

    override fun existsById(id: Long): Boolean = categoryJpaRepository.existsById(id)
}
