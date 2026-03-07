package com.heitor.finance.application.usecase

import com.heitor.finance.application.dto.CreateSubcategoryRequest
import com.heitor.finance.application.dto.SubcategoryResponse
import com.heitor.finance.application.port.input.UpdateSubcategoryUseCase
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

        val existing = subcategoryOutputPort.findById(id) ?: throw SubcategoryNotFoundException(id)

        if (!categoryOutputPort.existsById(request.categoryId)) {
            throw CategoryNotFoundException(request.categoryId)
        }

        if (existing.name != request.name &&
            subcategoryOutputPort.existsByNameInCategory(request.name, request.categoryId)
        ) {
            throw SubcategoryAlreadyExistsException(request.name, request.categoryId)
        }

        val updated = subcategoryOutputPort.update(
            existing.copy(name = request.name, categoryId = request.categoryId)
        )
        logger.info("Subcategory updated id={} name={}", updated.id, updated.name)
        return SubcategoryResponse(id = updated.id!!, name = updated.name, categoryId = updated.categoryId)
    }
}
