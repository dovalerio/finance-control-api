package com.heitor.finance.infrastructure.persistence.entity

import com.heitor.finance.domain.model.EntryType
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "entries")
class EntryEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val description: String,

    @Column(nullable = false, precision = 19, scale = 2)
    val amount: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: EntryType,

    @Column(nullable = false)
    val date: LocalDate,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    val category: CategoryEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subcategory_id")
    val subcategory: SubcategoryEntity? = null
)
