package com.heitor.finance.application.usecase

import com.heitor.finance.application.port.input.DeleteEntryUseCase
import com.heitor.finance.application.port.output.EntryOutputPort
import com.heitor.finance.domain.exception.EntryNotFoundException
import org.apache.logging.log4j.LogManager

class DeleteEntryUseCaseImpl(
    private val entryOutputPort: EntryOutputPort
) : DeleteEntryUseCase {

    private val logger = LogManager.getLogger(DeleteEntryUseCaseImpl::class.java)

    override fun execute(id: Long) {
        logger.debug("Deleting entry id={}", id)
        if (entryOutputPort.findById(id) == null) throw EntryNotFoundException(id)
        entryOutputPort.deleteById(id)
        logger.info("Entry deleted id={}", id)
    }
}
