package com.heitor.finance.application.port.input

import com.heitor.finance.application.dto.EntryResponse
import java.time.LocalDate

interface FindEntryUseCase {
    fun findAll(subcategoryId: Long? = null, startDate: LocalDate? = null, endDate: LocalDate? = null): List<EntryResponse>
    fun findById(id: Long): EntryResponse
}
