package com.heitor.finance.infrastructure.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "subcategories")
class SubcategoryEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val name: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    val category: CategoryEntity
)
