package com.heitor.finance.application.usecase

import com.heitor.finance.application.port.input.DeleteSubcategoryUseCase
import com.heitor.finance.application.port.output.SubcategoryOutputPort
import com.heitor.finance.domain.exception.SubcategoryNotFoundException
import org.apache.logging.log4j.LogManager

class DeleteSubcategoryUseCaseImpl(
    private val subcategoryOutputPort: SubcategoryOutputPort
) : DeleteSubcategoryUseCase {

    private val logger = LogManager.getLogger(DeleteSubcategoryUseCaseImpl::class.java)

    override fun execute(id: Long) {
        logger.debug("Deleting subcategory id={}", id)
        if (subcategoryOutputPort.findById(id) == null) throw SubcategoryNotFoundException(id)
        subcategoryOutputPort.deleteById(id)
        logger.info("Subcategory deleted id={}", id)
    }
}
