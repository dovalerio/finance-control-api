package com.heitor.finance.application.usecase

import com.heitor.finance.application.dto.SubcategoryResponse
import com.heitor.finance.application.port.input.FindSubcategoryUseCase
import com.heitor.finance.application.port.output.SubcategoryOutputPort
import com.heitor.finance.domain.exception.SubcategoryNotFoundException
import org.apache.logging.log4j.LogManager

class FindSubcategoryUseCaseImpl(
    private val subcategoryOutputPort: SubcategoryOutputPort
) : FindSubcategoryUseCase {

    private val logger = LogManager.getLogger(FindSubcategoryUseCaseImpl::class.java)

    override fun findAll(name: String?, categoryId: Long?): List<SubcategoryResponse> {
        logger.debug("Finding subcategories name={} categoryId={}", name ?: "none", categoryId ?: "none")
        return subcategoryOutputPort.findAll(name, categoryId)
            .map { SubcategoryResponse(id = it.id!!, name = it.name, categoryId = it.categoryId) }
    }

    override fun findById(id: Long): SubcategoryResponse {
        logger.debug("Finding subcategory id={}", id)
        val sub = subcategoryOutputPort.findById(id) ?: throw SubcategoryNotFoundException(id)
        return SubcategoryResponse(id = sub.id!!, name = sub.name, categoryId = sub.categoryId)
    }
}
