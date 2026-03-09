package com.heitor.finance.infrastructure.persistence.entity

import com.heitor.finance.domain.model.EntryType
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "lancamento")
class EntryEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lancamento")
    val id: Long? = null,

    @Column(name = "comentario", nullable = false)
    val description: String,

    @Column(name = "valor", nullable = false, precision = 19, scale = 2)
    val amount: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    val type: EntryType,

    @Column(name = "data", nullable = false)
    val date: LocalDate,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", nullable = false)
    val category: CategoryEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_subcategoria")
    val subcategory: SubcategoryEntity? = null
)
