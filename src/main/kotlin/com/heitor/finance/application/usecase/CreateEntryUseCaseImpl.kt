package com.heitor.finance.application.usecase

import com.heitor.finance.application.dto.CreateEntryRequest
import com.heitor.finance.application.dto.EntryResponse
import com.heitor.finance.application.port.input.CreateEntryUseCase
import com.heitor.finance.application.port.output.EntryOutputPort
import com.heitor.finance.application.port.output.SubcategoryOutputPort
import com.heitor.finance.domain.exception.SubcategoryNotFoundException
import com.heitor.finance.domain.model.Entry
import com.heitor.finance.domain.model.EntryType
import com.heitor.finance.domain.model.signedValue
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

        val subcategoryId = request.subcategoryId!!
        val subcategory = subcategoryOutputPort.findById(subcategoryId)
            ?: run {
                logger.warn("Subcategory not found id={}", subcategoryId)
                throw SubcategoryNotFoundException(subcategoryId)
            }

        val value = request.value!!
        val type = if (value >= BigDecimal.ZERO) EntryType.INCOME else EntryType.EXPENSE
        val amount = Money.of(value.abs())

        val entry = Entry(
            comment = request.comment ?: "",
            amount = amount,
            type = type,
            date = request.date ?: java.time.LocalDate.now(),
            categoryId = subcategory.categoryId,
            subcategoryId = subcategory.id
        )

        val saved = entryOutputPort.save(entry)
        logger.info("Entry created id={} type={} subcategoryId={}", saved.id, type, saved.subcategoryId)

        return EntryResponse(
            id = saved.id!!,
            value = saved.signedValue(),
            date = saved.date,
            subcategoryId = saved.subcategoryId,
            comment = saved.comment.ifBlank { null }
        )
    }
}
