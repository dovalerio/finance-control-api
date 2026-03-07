package com.heitor.finance.application.usecase

import com.heitor.finance.application.dto.CreateSubcategoryRequest
import com.heitor.finance.application.dto.SubcategoryResponse
import com.heitor.finance.application.port.input.CreateSubcategoryUseCase
import com.heitor.finance.application.port.output.CategoryOutputPort
import com.heitor.finance.application.port.output.SubcategoryOutputPort
import com.heitor.finance.domain.exception.CategoryNotFoundException
import com.heitor.finance.domain.exception.SubcategoryAlreadyExistsException
import com.heitor.finance.domain.model.Subcategory
import org.apache.logging.log4j.LogManager

class CreateSubcategoryUseCaseImpl(
    private val subcategoryOutputPort: SubcategoryOutputPort,
    private val categoryOutputPort: CategoryOutputPort
) : CreateSubcategoryUseCase {

    private val logger = LogManager.getLogger(CreateSubcategoryUseCaseImpl::class.java)

    override fun execute(request: CreateSubcategoryRequest): SubcategoryResponse {
        logger.debug("Creating subcategory name={} categoryId={}", request.name, request.categoryId)

        if (!categoryOutputPort.existsById(request.categoryId)) {
            logger.warn("Category not found categoryId={}", request.categoryId)
            throw CategoryNotFoundException(request.categoryId)
        }

        if (subcategoryOutputPort.existsByNameInCategory(request.name, request.categoryId)) {
            logger.warn("Subcategory already exists name={} categoryId={}", request.name, request.categoryId)
            throw SubcategoryAlreadyExistsException(request.name, request.categoryId)
        }

        val saved = subcategoryOutputPort.save(
            Subcategory(name = request.name, categoryId = request.categoryId)
        )
        logger.info("Subcategory created id={} name={} categoryId={}", saved.id, saved.name, saved.categoryId)
        return SubcategoryResponse(id = saved.id!!, name = saved.name, categoryId = saved.categoryId)
    }
}
