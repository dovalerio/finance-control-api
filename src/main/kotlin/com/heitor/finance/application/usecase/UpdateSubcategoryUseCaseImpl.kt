package com.heitor.finance.application.usecase

import com.heitor.finance.application.dto.CreateSubcategoryRequest
import com.heitor.finance.application.dto.SubcategoryResponse
import com.heitor.finance.application.dto.toResponse
import com.heitor.finance.application.port.input.UpdateSubcategoryUseCase
import com.heitor.finance.application.util.orThrow
import com.heitor.finance.application.port.output.CategoryOutputPort
import com.heitor.finance.application.port.output.SubcategoryOutputPort
import com.heitor.finance.domain.exception.CategoryNotFoundException
import com.heitor.finance.domain.exception.SubcategoryAlreadyExistsException
import com.heitor.finance.domain.exception.SubcategoryNotFoundException
import org.apache.logging.log4j.LogManager

class UpdateSubcategoryUseCaseImpl(
    private val subcategoryOutputPort: SubcategoryOutputPort,
    private val categoryOutputPort: CategoryOutputPort
) : UpdateSubcategoryUseCase {

    private val logger = LogManager.getLogger(UpdateSubcategoryUseCaseImpl::class.java)

    override fun execute(id: Long, request: CreateSubcategoryRequest): SubcategoryResponse {
        logger.debug("Updating subcategory id={} name={}", id, request.name)

        val existing = subcategoryOutputPort.findById(id).orThrow { SubcategoryNotFoundException(id) }
        val name = request.name!!
        val categoryId = request.categoryId!!

        if (!categoryOutputPort.existsById(categoryId)) {
            throw CategoryNotFoundException(categoryId)
        }

        if (existing.name != name &&
            subcategoryOutputPort.existsByNameInCategory(name, categoryId)
        ) {
            throw SubcategoryAlreadyExistsException(name, categoryId)
        }

        val updated = subcategoryOutputPort.update(
            existing.copy(name = name, categoryId = categoryId)
        )
        logger.info("Subcategory updated id={} name={}", updated.id, updated.name)
        return updated.toResponse()
    }
}
