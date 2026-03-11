package com.heitor.finance.application.usecase

import com.heitor.finance.application.dto.EntryResponse
import com.heitor.finance.application.dto.toResponse
import com.heitor.finance.application.port.input.FindEntryUseCase
import com.heitor.finance.application.port.output.EntryOutputPort
import com.heitor.finance.domain.exception.EntryNotFoundException
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

}
