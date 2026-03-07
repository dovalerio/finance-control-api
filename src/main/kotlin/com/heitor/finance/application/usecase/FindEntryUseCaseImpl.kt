package com.heitor.finance.application.usecase

import com.heitor.finance.application.dto.EntryResponse
import com.heitor.finance.application.port.input.FindEntryUseCase
import com.heitor.finance.application.port.output.EntryOutputPort
import com.heitor.finance.domain.exception.EntryNotFoundException
import com.heitor.finance.domain.model.Entry
import com.heitor.finance.domain.model.EntryType
import org.apache.logging.log4j.LogManager
import java.time.LocalDate

class FindEntryUseCaseImpl(
    private val entryOutputPort: EntryOutputPort
) : FindEntryUseCase {

    private val logger = LogManager.getLogger(FindEntryUseCaseImpl::class.java)

    override fun findAll(subcategoryId: Long?, startDate: LocalDate?, endDate: LocalDate?): List<EntryResponse> {
        logger.debug("Finding entries subcategoryId={} startDate={} endDate={}", subcategoryId, startDate, endDate)
        return entryOutputPort.findByFilters(subcategoryId, startDate, endDate).map { it.toResponse() }
    }

    override fun findById(id: Long): EntryResponse {
        logger.debug("Finding entry id={}", id)
        val entry = entryOutputPort.findById(id) ?: throw EntryNotFoundException(id)
        return entry.toResponse()
    }

    private fun Entry.toResponse(): EntryResponse {
        val value = if (type == EntryType.INCOME) amount.amount else amount.amount.negate()
        return EntryResponse(
            id = id!!,
            value = value,
            date = date,
            subcategoryId = subcategoryId,
            comment = description.ifBlank { null }
        )
    }
}
