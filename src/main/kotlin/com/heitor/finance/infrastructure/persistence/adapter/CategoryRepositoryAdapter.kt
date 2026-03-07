package com.heitor.finance.infrastructure.persistence.adapter

import com.heitor.finance.application.port.output.CategoryOutputPort
import com.heitor.finance.domain.model.Category
import com.heitor.finance.infrastructure.persistence.mapper.CategoryMapper
import com.heitor.finance.infrastructure.persistence.repository.CategoryJpaRepository
import org.apache.logging.log4j.LogManager
import org.springframework.stereotype.Component

@Component
class CategoryRepositoryAdapter(
    private val categoryJpaRepository: CategoryJpaRepository
) : CategoryOutputPort {

    private val logger = LogManager.getLogger(CategoryRepositoryAdapter::class.java)

    override fun findAll(name: String?): List<Category> {
        logger.debug("Finding all categories filter={}" , name ?: "none")
        val entities = if (name == null) {
            categoryJpaRepository.findAllWithSubcategories()
        } else {
            categoryJpaRepository.findAllWithSubcategoriesByName(name)
        }
        return entities.map(CategoryMapper::toDomain)
    }

    override fun findById(id: Long): Category? {
        logger.debug("Finding category id={}", id)
        return categoryJpaRepository.findByIdWithSubcategories(id).map(CategoryMapper::toDomain).orElse(null)
    }

    override fun save(category: Category): Category {
        logger.debug("Saving category name={}", category.name)
        val entity = CategoryMapper.toEntity(category)
        return CategoryMapper.toDomain(categoryJpaRepository.save(entity))
    }

    override fun update(category: Category): Category {
        logger.debug("Updating category id={} name={}", category.id, category.name)
        val entity = CategoryMapper.toEntity(category)
        return CategoryMapper.toDomain(categoryJpaRepository.save(entity))
    }

    override fun deleteById(id: Long) {
        logger.debug("Deleting category id={}", id)
        categoryJpaRepository.deleteById(id)
    }

    override fun existsById(id: Long): Boolean = categoryJpaRepository.existsById(id)

    override fun existsByName(name: String): Boolean = categoryJpaRepository.existsByName(name)
}
