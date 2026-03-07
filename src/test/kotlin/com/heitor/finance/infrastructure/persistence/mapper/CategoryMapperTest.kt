package com.heitor.finance.infrastructure.persistence.mapper

import com.heitor.finance.domain.model.Category
import com.heitor.finance.domain.model.Subcategory
import com.heitor.finance.infrastructure.persistence.entity.CategoryEntity
import com.heitor.finance.infrastructure.persistence.entity.SubcategoryEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class CategoryMapperTest {

    @Test
    fun `toDomain should map CategoryEntity to Category including subcategories`() {
        val categoryEntity = CategoryEntity(
            id = 1L,
            name = "Food",
            subcategories = listOf(
                SubcategoryEntity(id = 10L, name = "Snacks", category = CategoryEntity(id = 1L, name = "Food"))
            )
        )

        val category = CategoryMapper.toDomain(categoryEntity)

        assertEquals(1L, category.id)
        assertEquals("Food", category.name)
        assertEquals(1, category.subcategories.size)
        assertEquals(10L, category.subcategories[0].id)
        assertEquals("Snacks", category.subcategories[0].name)
        assertEquals(1L, category.subcategories[0].categoryId)
    }

    @Test
    fun `toDomain should map CategoryEntity with empty subcategories`() {
        val entity = CategoryEntity(id = 2L, name = "Transport", subcategories = emptyList())

        val category = CategoryMapper.toDomain(entity)

        assertEquals(2L, category.id)
        assertEquals("Transport", category.name)
        assertEquals(0, category.subcategories.size)
    }

    @Test
    fun `toEntity should map Category to CategoryEntity`() {
        val category = Category(id = 5L, name = "Health")

        val entity = CategoryMapper.toEntity(category)

        assertEquals(5L, entity.id)
        assertEquals("Health", entity.name)
    }

    @Test
    fun `toEntity should map Category with null id`() {
        val category = Category(id = null, name = "Leisure")

        val entity = CategoryMapper.toEntity(category)

        assertNull(entity.id)
        assertEquals("Leisure", entity.name)
    }

    @Test
    fun `toDomain should map CategoryEntity with null id`() {
        val entity = CategoryEntity(id = null, name = "NewCategory")

        val category = CategoryMapper.toDomain(entity)

        assertNull(category.id)
        assertEquals("NewCategory", category.name)
    }
}
