package com.heitor.finance.infrastructure.controller

import com.heitor.finance.application.dto.CreateEntryRequest
import com.heitor.finance.application.dto.EntryResponse
import com.heitor.finance.application.port.input.CreateEntryUseCase
import com.heitor.finance.application.port.input.DeleteEntryUseCase
import com.heitor.finance.application.port.input.FindEntryUseCase
import com.heitor.finance.application.port.input.UpdateEntryUseCase
import jakarta.validation.Valid
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/v1/entries")
class EntryController(
    private val createEntryUseCase: CreateEntryUseCase,
    private val findEntryUseCase: FindEntryUseCase,
    private val updateEntryUseCase: UpdateEntryUseCase,
    private val deleteEntryUseCase: DeleteEntryUseCase
) {

    @GetMapping
    fun findAll(
        @RequestParam(required = false) subcategoryId: Long?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate?
    ): ResponseEntity<List<EntryResponse>> =
        ResponseEntity.ok(findEntryUseCase.findAll(subcategoryId, startDate, endDate))

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<EntryResponse> =
        ResponseEntity.ok(findEntryUseCase.findById(id))

    @PostMapping
    fun create(@Valid @RequestBody request: CreateEntryRequest): ResponseEntity<EntryResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(createEntryUseCase.execute(request))

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: CreateEntryRequest
    ): ResponseEntity<EntryResponse> =
        ResponseEntity.ok(updateEntryUseCase.execute(id, request))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        deleteEntryUseCase.execute(id)
        return ResponseEntity.noContent().build()
    }
}
