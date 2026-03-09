package com.heitor.finance.application.port.output

import com.heitor.finance.domain.model.Entry
import com.heitor.finance.domain.valueobject.Period
import java.time.LocalDate

interface EntryOutputPort {
    fun findAll(): List<Entry>
    fun findById(id: Long): Entry?
    fun findByFilters(subcategoryId: Long?, startDate: LocalDate?, endDate: LocalDate?): List<Entry>
    fun findByPeriod(period: Period): List<Entry>
    fun findByPeriodAndCategoryId(period: Period, categoryId: Long): List<Entry>
    fun save(entry: Entry): Entry
    fun deleteById(id: Long)
    fun existsBySubcategoryId(subcategoryId: Long): Boolean
}
