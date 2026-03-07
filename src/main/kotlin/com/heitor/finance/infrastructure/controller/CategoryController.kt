package com.heitor.finance.infrastructure.controller

import com.heitor.finance.application.dto.CategoryResponse
import com.heitor.finance.application.dto.CreateCategoryRequest
import com.heitor.finance.application.port.input.CreateCategoryUseCase
import com.heitor.finance.application.port.input.DeleteCategoryUseCase
import com.heitor.finance.application.port.input.FindCategoryUseCase
import com.heitor.finance.application.port.input.UpdateCategoryUseCase
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/categorias")
class CategoryController(
    private val createCategoryUseCase: CreateCategoryUseCase,
    private val findCategoryUseCase: FindCategoryUseCase,
    private val updateCategoryUseCase: UpdateCategoryUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase
) {

    @GetMapping
    fun findAll(@RequestParam("nome", required = false) name: String?): ResponseEntity<List<CategoryResponse>> =
        ResponseEntity.ok(findCategoryUseCase.findAll(name))

    @GetMapping("/{id_categoria}")
    fun findById(@PathVariable("id_categoria") id: Long): ResponseEntity<CategoryResponse> =
        ResponseEntity.ok(findCategoryUseCase.findById(id))

    @PutMapping("/{id_categoria}")
    fun update(
        @PathVariable("id_categoria") id: Long,
        @Valid @RequestBody request: CreateCategoryRequest
    ): ResponseEntity<CategoryResponse> =
        ResponseEntity.ok(updateCategoryUseCase.execute(id, request))

    @PostMapping
    fun create(@Valid @RequestBody request: CreateCategoryRequest): ResponseEntity<CategoryResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(createCategoryUseCase.execute(request))

    @DeleteMapping("/{id_categoria}")
    fun delete(@PathVariable("id_categoria") id: Long): ResponseEntity<Void> {
        deleteCategoryUseCase.execute(id)
        return ResponseEntity.noContent().build()
    }
}
