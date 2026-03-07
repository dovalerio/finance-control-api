package com.heitor.finance.infrastructure.persistence.mapper

import com.heitor.finance.domain.model.Subcategory
import com.heitor.finance.infrastructure.persistence.entity.CategoryEntity
import com.heitor.finance.infrastructure.persistence.entity.SubcategoryEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class SubcategoryMapperTest {

    private val categoryEntity = CategoryEntity(id = 1L, name = "Food")

    @Test
    fun `toDomain should map SubcategoryEntity to Subcategory`() {
        val entity = SubcategoryEntity(id = 10L, name = "Snacks", category = categoryEntity)

        val subcategory = SubcategoryMapper.toDomain(entity)

        assertEquals(10L, subcategory.id)
        assertEquals("Snacks", subcategory.name)
        assertEquals(1L, subcategory.categoryId)
    }

    @Test
    fun `toDomain should map SubcategoryEntity with null id`() {
        val entity = SubcategoryEntity(id = null, name = "Beverages", category = categoryEntity)

        val subcategory = SubcategoryMapper.toDomain(entity)

        assertNull(subcategory.id)
        assertEquals("Beverages", subcategory.name)
        assertEquals(1L, subcategory.categoryId)
    }

    @Test
    fun `toEntity should map Subcategory to SubcategoryEntity`() {
        val subcategory = Subcategory(id = 10L, name = "Snacks", categoryId = 1L)

        val entity = SubcategoryMapper.toEntity(subcategory, categoryEntity)

        assertEquals(10L, entity.id)
        assertEquals("Snacks", entity.name)
        assertEquals(1L, entity.category.id)
    }

    @Test
    fun `toEntity should map Subcategory with null id`() {
        val subcategory = Subcategory(id = null, name = "Juices", categoryId = 1L)

        val entity = SubcategoryMapper.toEntity(subcategory, categoryEntity)

        assertNull(entity.id)
        assertEquals("Juices", entity.name)
        assertEquals(categoryEntity, entity.category)
    }
}
