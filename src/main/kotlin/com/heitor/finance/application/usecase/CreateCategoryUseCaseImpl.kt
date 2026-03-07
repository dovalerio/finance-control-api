package com.heitor.finance.application.usecase

import com.heitor.finance.application.dto.CreateCategoryRequest
import com.heitor.finance.application.dto.CategoryResponse
import com.heitor.finance.application.port.input.CreateCategoryUseCase
import com.heitor.finance.application.port.output.CategoryOutputPort
import com.heitor.finance.domain.exception.CategoryAlreadyExistsException
import com.heitor.finance.domain.model.Category
import org.apache.logging.log4j.LogManager

class CreateCategoryUseCaseImpl(
    private val categoryOutputPort: CategoryOutputPort
) : CreateCategoryUseCase {

    private val logger = LogManager.getLogger(CreateCategoryUseCaseImpl::class.java)

    override fun execute(request: CreateCategoryRequest): CategoryResponse {
        logger.debug("Creating category name={}", request.name)

        if (categoryOutputPort.existsByName(request.name)) {
            logger.warn("Category already exists name={}", request.name)
            throw CategoryAlreadyExistsException(request.name)
        }

        val saved = categoryOutputPort.save(Category(name = request.name))
        logger.info("Category created id={} name={}", saved.id, saved.name)
        return CategoryResponse(id = saved.id!!, name = saved.name)
    }
}
