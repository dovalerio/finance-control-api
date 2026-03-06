package com.heitor.finance.infrastructure.persistence.mapper

import com.heitor.finance.domain.model.Entry
import com.heitor.finance.domain.valueobject.Money
import com.heitor.finance.infrastructure.persistence.entity.CategoryEntity
import com.heitor.finance.infrastructure.persistence.entity.EntryEntity
import com.heitor.finance.infrastructure.persistence.entity.SubcategoryEntity

object EntryMapper {

    fun toDomain(entity: EntryEntity): Entry = Entry(
        id = entity.id,
        description = entity.description,
        amount = Money.of(entity.amount),
        type = entity.type,
        date = entity.date,
        categoryId = entity.category.id!!,
        subcategoryId = entity.subcategory?.id
    )

    fun toEntity(domain: Entry, category: CategoryEntity, subcategory: SubcategoryEntity?): EntryEntity =
        EntryEntity(
            id = domain.id,
            description = domain.description,
            amount = domain.amount.amount,
            type = domain.type,
            date = domain.date,
            category = category,
            subcategory = subcategory
        )
}
