package com.heitor.finance.infrastructure.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "categoria")
class CategoryEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    val id: Long? = null,

    @Column(name = "nome", nullable = false, unique = true)
    val name: String,

    @OneToMany(mappedBy = "category", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val subcategories: List<SubcategoryEntity> = emptyList()
)
