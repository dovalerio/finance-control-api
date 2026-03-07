package com.heitor.finance.infrastructure.persistence.mapper

import com.heitor.finance.domain.model.Category
import com.heitor.finance.domain.model.Subcategory
import com.heitor.finance.infrastructure.persistence.entity.CategoryEntity
import com.heitor.finance.infrastructure.persistence.entity.SubcategoryEntity

object CategoryMapper {

    fun toDomain(entity: CategoryEntity): Category = Category(
        id = entity.id,
        name = entity.name,
        subcategories = entity.subcategories.map { SubcategoryMapper.toDomain(it) }
    )

    fun toEntity(domain: Category): CategoryEntity = CategoryEntity(
        id = domain.id,
        name = domain.name
    )
}

object SubcategoryMapper {

    fun toDomain(entity: SubcategoryEntity): Subcategory = Subcategory(
        id = entity.id,
        name = entity.name,
        categoryId = entity.category.id!!
    )

    fun toEntity(domain: Subcategory, categoryEntity: CategoryEntity): SubcategoryEntity = SubcategoryEntity(
        id = domain.id,
        name = domain.name,
        category = categoryEntity
    )
}
