package com.heitor.finance.application.usecase

import com.heitor.finance.application.dto.CreateEntryRequest
import com.heitor.finance.application.dto.EntryResponse
import com.heitor.finance.application.port.input.UpdateEntryUseCase
import com.heitor.finance.application.port.output.EntryOutputPort
import com.heitor.finance.application.port.output.SubcategoryOutputPort
import com.heitor.finance.domain.exception.EntryNotFoundException
import com.heitor.finance.domain.exception.SubcategoryNotFoundException
import com.heitor.finance.domain.model.EntryType
import com.heitor.finance.domain.valueobject.Money
import org.apache.logging.log4j.LogManager
import java.math.BigDecimal

class UpdateEntryUseCaseImpl(
    private val entryOutputPort: EntryOutputPort,
    private val subcategoryOutputPort: SubcategoryOutputPort
) : UpdateEntryUseCase {

    private val logger = LogManager.getLogger(UpdateEntryUseCaseImpl::class.java)

    override fun execute(id: Long, request: CreateEntryRequest): EntryResponse {
        logger.debug("Updating entry id={} value={}", id, request.value)

        val existing = entryOutputPort.findById(id) ?: throw EntryNotFoundException(id)

        val subcategoryId = request.subcategoryId!!
        val subcategory = subcategoryOutputPort.findById(subcategoryId)
            ?: throw SubcategoryNotFoundException(subcategoryId)

        val value = request.value!!
        val type = if (value >= BigDecimal.ZERO) EntryType.INCOME else EntryType.EXPENSE
        val amount = Money.of(value.abs())

        val updated = entryOutputPort.save(
            existing.copy(
                amount = amount,
                type = type,
                date = request.date ?: existing.date,
                categoryId = subcategory.categoryId,
                subcategoryId = subcategory.id,
                comment = request.comment ?: ""
            )
        )

        logger.info("Entry updated id={} type={}", updated.id, type)
        val savedValue = if (updated.type == EntryType.INCOME) updated.amount.amount else updated.amount.amount.negate()
        return EntryResponse(
            id = updated.id!!,
            value = savedValue,
            date = updated.date,
            subcategoryId = updated.subcategoryId,
            comment = updated.comment.ifBlank { null }
        )
    }
}
