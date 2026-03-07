package com.heitor.finance.application.port.input

import com.heitor.finance.application.dto.CreateEntryRequest
import com.heitor.finance.application.dto.EntryResponse

interface UpdateEntryUseCase {
    fun execute(id: Long, request: CreateEntryRequest): EntryResponse
}
