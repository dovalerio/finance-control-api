package com.heitor.finance.infrastructure.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "categories")
class CategoryEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    val name: String,

    @OneToMany(mappedBy = "category", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val subcategories: List<SubcategoryEntity> = emptyList()
)
