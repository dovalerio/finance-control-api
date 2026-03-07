package com.heitor.finance.infrastructure.controller

import com.heitor.finance.application.dto.CreateSubcategoryRequest
import com.heitor.finance.application.dto.SubcategoryResponse
import com.heitor.finance.application.port.input.CreateSubcategoryUseCase
import com.heitor.finance.application.port.input.DeleteSubcategoryUseCase
import com.heitor.finance.application.port.input.FindSubcategoryUseCase
import com.heitor.finance.application.port.input.UpdateSubcategoryUseCase
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/subcategories")
class SubcategoryController(
    private val createSubcategoryUseCase: CreateSubcategoryUseCase,
    private val findSubcategoryUseCase: FindSubcategoryUseCase,
    private val updateSubcategoryUseCase: UpdateSubcategoryUseCase,
    private val deleteSubcategoryUseCase: DeleteSubcategoryUseCase
) {

    @GetMapping
    fun findAll(
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) categoryId: Long?
    ): ResponseEntity<List<SubcategoryResponse>> =
        ResponseEntity.ok(findSubcategoryUseCase.findAll(name, categoryId))

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<SubcategoryResponse> =
        ResponseEntity.ok(findSubcategoryUseCase.findById(id))

    @PostMapping
    fun create(@Valid @RequestBody request: CreateSubcategoryRequest): ResponseEntity<SubcategoryResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(createSubcategoryUseCase.execute(request))

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: CreateSubcategoryRequest
    ): ResponseEntity<SubcategoryResponse> =
        ResponseEntity.ok(updateSubcategoryUseCase.execute(id, request))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        deleteSubcategoryUseCase.execute(id)
        return ResponseEntity.noContent().build()
    }
}
