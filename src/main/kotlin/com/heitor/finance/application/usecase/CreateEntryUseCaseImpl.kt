package com.heitor.finance.application.usecase

import com.heitor.finance.application.dto.CreateEntryRequest
import com.heitor.finance.application.dto.EntryResponse
import com.heitor.finance.application.port.input.CreateEntryUseCase
import com.heitor.finance.application.port.output.EntryOutputPort
import com.heitor.finance.application.port.output.SubcategoryOutputPort
import com.heitor.finance.domain.exception.SubcategoryNotFoundException
import com.heitor.finance.domain.model.Entry
import com.heitor.finance.domain.model.EntryType
import com.heitor.finance.domain.valueobject.Money
import org.apache.logging.log4j.LogManager
import java.math.BigDecimal

class CreateEntryUseCaseImpl(
    private val entryOutputPort: EntryOutputPort,
    private val subcategoryOutputPort: SubcategoryOutputPort
) : CreateEntryUseCase {

    private val logger = LogManager.getLogger(CreateEntryUseCaseImpl::class.java)

    override fun execute(request: CreateEntryRequest): EntryResponse {
        logger.debug("Creating entry value={} subcategoryId={}", request.value, request.subcategoryId)

        val subcategory = subcategoryOutputPort.findById(request.subcategoryId)
            ?: run {
                logger.warn("Subcategory not found id={}", request.subcategoryId)
                throw SubcategoryNotFoundException(request.subcategoryId)
            }

        val type = if (request.value >= BigDecimal.ZERO) EntryType.INCOME else EntryType.EXPENSE
        val amount = Money.of(request.value.abs())

        val entry = Entry(
            description = request.comment ?: "",
            amount = amount,
            type = type,
            date = request.date,
            categoryId = subcategory.categoryId,
            subcategoryId = subcategory.id
        )

        val saved = entryOutputPort.save(entry)
        logger.info("Entry created id={} type={} subcategoryId={}", saved.id, type, saved.subcategoryId)

        val savedValue = if (saved.type == EntryType.INCOME) saved.amount.amount else saved.amount.amount.negate()
        return EntryResponse(
            id = saved.id!!,
            value = savedValue,
            date = saved.date,
            subcategoryId = saved.subcategoryId,
            comment = saved.description.ifBlank { null }
        )
    }
}
