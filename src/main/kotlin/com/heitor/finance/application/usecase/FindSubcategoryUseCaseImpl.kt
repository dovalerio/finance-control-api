package com.heitor.finance.application.usecase

import com.heitor.finance.application.dto.SubcategoryResponse
import com.heitor.finance.application.dto.toResponse
import com.heitor.finance.application.port.input.FindSubcategoryUseCase
import com.heitor.finance.application.util.orThrow
import com.heitor.finance.application.port.output.SubcategoryOutputPort
import com.heitor.finance.domain.exception.SubcategoryNotFoundException
import org.apache.logging.log4j.LogManager

class FindSubcategoryUseCaseImpl(
    private val subcategoryOutputPort: SubcategoryOutputPort
) : FindSubcategoryUseCase {

    private val logger = LogManager.getLogger(FindSubcategoryUseCaseImpl::class.java)

    override fun findAll(name: String?, subcategoryId: Long?): List<SubcategoryResponse> {
        logger.debug("Finding subcategories name={} subcategoryId={}", name ?: "none", subcategoryId ?: "none")
        return subcategoryOutputPort.findAll(name, subcategoryId).map { it.toResponse() }
    }

    override fun findById(id: Long): SubcategoryResponse {
        logger.debug("Finding subcategory id={}", id)
        val sub = subcategoryOutputPort.findById(id).orThrow { SubcategoryNotFoundException(id) }
        return sub.toResponse()
    }
}
