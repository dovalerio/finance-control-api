package com.heitor.finance.infrastructure.persistence.adapter

import com.heitor.finance.application.port.output.SubcategoryOutputPort
import com.heitor.finance.domain.exception.CategoryNotFoundException
import com.heitor.finance.domain.model.Subcategory
import com.heitor.finance.infrastructure.persistence.mapper.SubcategoryMapper
import com.heitor.finance.infrastructure.persistence.repository.CategoryJpaRepository
import com.heitor.finance.infrastructure.persistence.repository.SubcategoryJpaRepository
import org.apache.logging.log4j.LogManager
import org.springframework.stereotype.Component

@Component
class SubcategoryRepositoryAdapter(
    private val subcategoryJpaRepository: SubcategoryJpaRepository,
    private val categoryJpaRepository: CategoryJpaRepository
) : SubcategoryOutputPort {

    private val logger = LogManager.getLogger(SubcategoryRepositoryAdapter::class.java)

    override fun findAll(name: String?, categoryId: Long?): List<Subcategory> {
        logger.debug("Finding subcategories name={} categoryId={}", name ?: "none", categoryId ?: "none")
        val entities = when {
            name != null && categoryId != null -> subcategoryJpaRepository.findByNameContainingIgnoreCaseAndCategoryId(name, categoryId)
            name != null -> subcategoryJpaRepository.findByNameContainingIgnoreCase(name)
            categoryId != null -> subcategoryJpaRepository.findByCategoryId(categoryId)
            else -> subcategoryJpaRepository.findAll()
        }
        return entities.map(SubcategoryMapper::toDomain)
    }

    override fun findById(id: Long): Subcategory? {
        logger.debug("Finding subcategory id={}", id)
        return subcategoryJpaRepository.findById(id).map(SubcategoryMapper::toDomain).orElse(null)
    }

    override fun save(subcategory: Subcategory): Subcategory {
        logger.debug("Saving subcategory name={} categoryId={}", subcategory.name, subcategory.categoryId)
        val category = categoryJpaRepository.findById(subcategory.categoryId)
            .orElseThrow { CategoryNotFoundException(subcategory.categoryId) }
        val entity = SubcategoryMapper.toEntity(subcategory, category)
        return SubcategoryMapper.toDomain(subcategoryJpaRepository.save(entity))
    }

    override fun update(subcategory: Subcategory): Subcategory {
        logger.debug("Updating subcategory id={} name={}", subcategory.id, subcategory.name)
        val category = categoryJpaRepository.findById(subcategory.categoryId)
            .orElseThrow { CategoryNotFoundException(subcategory.categoryId) }
        val entity = SubcategoryMapper.toEntity(subcategory, category)
        return SubcategoryMapper.toDomain(subcategoryJpaRepository.save(entity))
    }

    override fun deleteById(id: Long) {
        logger.debug("Deleting subcategory id={}", id)
        subcategoryJpaRepository.deleteById(id)
    }

    override fun existsByNameInCategory(name: String, categoryId: Long): Boolean =
        subcategoryJpaRepository.existsByNameAndCategoryId(name, categoryId)
}
