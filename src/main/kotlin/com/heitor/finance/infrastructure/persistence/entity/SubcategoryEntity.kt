package com.heitor.finance.infrastructure.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "subcategoria")
class SubcategoryEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_subcategoria")
    val id: Long? = null,

    @Column(name = "nome", nullable = false)
    val name: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", nullable = false)
    val category: CategoryEntity
)
