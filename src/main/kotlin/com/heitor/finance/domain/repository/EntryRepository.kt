package com.heitor.finance.domain.repository

import com.heitor.finance.domain.model.Entry
import com.heitor.finance.domain.valueobject.Period

interface EntryRepository {
    fun findAll(): List<Entry>
    fun findById(id: Long): Entry?
    fun findByPeriodAndCategoryId(period: Period, categoryId: Long): List<Entry>
    fun save(entry: Entry): Entry
    fun deleteById(id: Long)
}
