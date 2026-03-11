package com.heitor.finance.application.usecase

import com.heitor.finance.application.dto.CreateCategoryRequest
import com.heitor.finance.application.dto.CategoryResponse
import com.heitor.finance.application.dto.toResponse
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

        val name = request.name!!
        if (categoryOutputPort.existsByName(name)) {
            logger.warn("Category already exists name={}", name)
            throw CategoryAlreadyExistsException(name)
        }

        val saved = categoryOutputPort.save(Category(name = name))
        logger.info("Category created id={} name={}", saved.id, saved.name)
        return saved.toResponse()
    }
}
