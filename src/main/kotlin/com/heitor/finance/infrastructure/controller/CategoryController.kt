package com.heitor.finance.infrastructure.controller

import com.heitor.finance.application.dto.CategoryResponse
import com.heitor.finance.application.dto.CreateCategoryRequest
import com.heitor.finance.application.port.input.CreateCategoryUseCase
import com.heitor.finance.application.port.input.FindCategoryUseCase
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/categorias")
class CategoryController(
    private val createCategoryUseCase: CreateCategoryUseCase,
    private val findCategoryUseCase: FindCategoryUseCase
) {

    @GetMapping
    fun findAll(): ResponseEntity<List<CategoryResponse>> =
        ResponseEntity.ok(findCategoryUseCase.findAll())

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<CategoryResponse> =
        ResponseEntity.ok(findCategoryUseCase.findById(id))

    @PostMapping
    fun create(@Valid @RequestBody request: CreateCategoryRequest): ResponseEntity<CategoryResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(createCategoryUseCase.execute(request))
}
