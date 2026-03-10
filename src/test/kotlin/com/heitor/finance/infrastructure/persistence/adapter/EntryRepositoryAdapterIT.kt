package com.heitor.finance.infrastructure.persistence.adapter

import com.heitor.finance.domain.model.Category
import com.heitor.finance.domain.model.Entry
import com.heitor.finance.domain.model.EntryType
import com.heitor.finance.domain.model.Subcategory
import com.heitor.finance.domain.valueobject.Money
import com.heitor.finance.domain.valueobject.Period
import com.heitor.finance.infrastructure.persistence.repository.SubcategoryJpaRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import java.math.BigDecimal
import java.time.LocalDate

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class EntryRepositoryAdapterIT {

    companion object {
        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", TestDatabase.container::getJdbcUrl)
            registry.add("spring.datasource.username", TestDatabase.container::getUsername)
            registry.add("spring.datasource.password", TestDatabase.container::getPassword)
        }
    }

    @Autowired private lateinit var adapter: EntryRepositoryAdapter
    @Autowired private lateinit var categoryAdapter: CategoryRepositoryAdapter
    @Autowired private lateinit var subcategoryAdapter: SubcategoryRepositoryAdapter
    @Autowired private lateinit var subcategoryJpaRepository: SubcategoryJpaRepository
    @Autowired private lateinit var jdbcTemplate: JdbcTemplate

    private var savedCategoryId: Long = 0L
    private var savedSubcategoryId: Long = 0L

    @BeforeEach
    fun setup() {
        jdbcTemplate.execute("TRUNCATE TABLE lancamento, subcategoria, categoria RESTART IDENTITY CASCADE")
        val category = categoryAdapter.save(Category(name = "Transport"))
        savedCategoryId = category.id!!
        val subcategory = subcategoryAdapter.save(Subcategory(name = "Fuel", categoryId = savedCategoryId))
        savedSubcategoryId = subcategory.id!!
    }

    private fun income(
        date: LocalDate = LocalDate.of(2024, 3, 10),
        value: BigDecimal = BigDecimal("100.00"),
        subcategoryId: Long? = savedSubcategoryId,
        comment: String = ""
    ) = Entry(
        comment = comment,
        amount = Money.of(value),
        type = EntryType.INCOME,
        date = date,
        categoryId = savedCategoryId,
        subcategoryId = subcategoryId
    )

    private fun expense(
        date: LocalDate = LocalDate.of(2024, 3, 10),
        value: BigDecimal = BigDecimal("50.00"),
        subcategoryId: Long? = savedSubcategoryId
    ) = Entry(
        comment = "",
        amount = Money.of(value),
        type = EntryType.EXPENSE,
        date = date,
        categoryId = savedCategoryId,
        subcategoryId = subcategoryId
    )

    // --- save ---

    @Test
    fun `save returns INCOME entry with generated id`() {
        val saved = adapter.save(income())

        assertThat(saved.id).isNotNull()
        assertThat(saved.type).isEqualTo(EntryType.INCOME)
        assertThat(saved.amount.amount).isEqualByComparingTo(BigDecimal("100.00"))
        assertThat(saved.subcategoryId).isEqualTo(savedSubcategoryId)
        assertThat(saved.categoryId).isEqualTo(savedCategoryId)
    }

    @Test
    fun `save returns EXPENSE entry with generated id`() {
        val saved = adapter.save(expense())

        assertThat(saved.id).isNotNull()
        assertThat(saved.type).isEqualTo(EntryType.EXPENSE)
    }

    @Test
    fun `save returns entry without subcategory when subcategoryId is null`() {
        val saved = adapter.save(income(subcategoryId = null))

        assertThat(saved.subcategoryId).isNull()
    }

    // --- findAll ---

    @Test
    fun `findAll returns all entries`() {
        adapter.save(income())
        adapter.save(expense())

        val result = adapter.findAll()

        assertThat(result).hasSize(2)
    }

    // --- findById ---

    @Test
    fun `findById returns entry when found`() {
        val saved = adapter.save(income(comment = "Salary"))

        val found = adapter.findById(saved.id!!)

        assertThat(found).isNotNull()
        assertThat(found!!.id).isEqualTo(saved.id)
        assertThat(found.comment).isEqualTo("Salary")
    }

    @Test
    fun `findById returns null when not found`() {
        assertThat(adapter.findById(999L)).isNull()
    }

    // --- findByFilters ---

    @Test
    fun `findByFilters with all null returns all entries`() {
        adapter.save(income())
        adapter.save(expense())

        val result = adapter.findByFilters(null, null, null)

        assertThat(result).hasSize(2)
    }

    @Test
    fun `findByFilters with subcategoryId returns entries for that subcategory`() {
        val other = subcategoryAdapter.save(Subcategory(name = "Bus", categoryId = savedCategoryId))
        adapter.save(income(subcategoryId = savedSubcategoryId))
        adapter.save(income(subcategoryId = other.id))

        val result = adapter.findByFilters(savedSubcategoryId, null, null)

        assertThat(result).hasSize(1)
        assertThat(result.first().subcategoryId).isEqualTo(savedSubcategoryId)
    }

    @Test
    fun `findByFilters with period returns entries within date range`() {
        adapter.save(income(date = LocalDate.of(2024, 1, 15)))
        adapter.save(income(date = LocalDate.of(2024, 3, 10)))

        val result = adapter.findByFilters(null, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 2, 28))

        assertThat(result).hasSize(1)
        assertThat(result.first().date).isEqualTo(LocalDate.of(2024, 1, 15))
    }

    @Test
    fun `findByFilters with subcategoryId and period returns filtered entries`() {
        adapter.save(income(date = LocalDate.of(2024, 1, 10), subcategoryId = savedSubcategoryId))
        adapter.save(income(date = LocalDate.of(2024, 3, 10), subcategoryId = savedSubcategoryId))

        val result = adapter.findByFilters(
            savedSubcategoryId,
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 1, 31)
        )

        assertThat(result).hasSize(1)
        assertThat(result.first().date).isEqualTo(LocalDate.of(2024, 1, 10))
    }

    // --- findByPeriod ---

    @Test
    fun `findByPeriod returns entries within date range`() {
        adapter.save(income(date = LocalDate.of(2024, 2, 5)))
        adapter.save(income(date = LocalDate.of(2024, 4, 1)))

        val result = adapter.findByPeriod(
            Period(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 3, 31))
        )

        assertThat(result).hasSize(1)
        assertThat(result.first().date).isEqualTo(LocalDate.of(2024, 2, 5))
    }

    // --- findByPeriodAndCategoryId ---

    @Test
    fun `findByPeriodAndCategoryId returns entries for category in period`() {
        val otherCategory = categoryAdapter.save(Category(name = "Food"))
        val otherSubcat = subcategoryAdapter.save(Subcategory(name = "Meal", categoryId = otherCategory.id!!))

        adapter.save(income(date = LocalDate.of(2024, 2, 5)))
        // entry for a different category — must be saved via raw JDBC since adapter validates category/subcategory
        jdbcTemplate.update(
            "INSERT INTO lancamento (comentario, valor, tipo, data, id_categoria, id_subcategoria) VALUES (?, ?, ?, ?, ?, ?)",
            "", BigDecimal("20.00"), "EXPENSE", LocalDate.of(2024, 2, 5), otherCategory.id, otherSubcat.id
        )

        val result = adapter.findByPeriodAndCategoryId(
            Period(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 3, 31)),
            savedCategoryId
        )

        assertThat(result).hasSize(1)
        assertThat(result.first().categoryId).isEqualTo(savedCategoryId)
    }

    // --- deleteById ---

    @Test
    fun `deleteById removes entry`() {
        val saved = adapter.save(income())

        adapter.deleteById(saved.id!!)

        assertThat(adapter.findById(saved.id!!)).isNull()
    }

    // --- existsBySubcategoryId ---

    @Test
    fun `existsBySubcategoryId returns true when entry exists for subcategory`() {
        adapter.save(income(subcategoryId = savedSubcategoryId))

        assertThat(adapter.existsBySubcategoryId(savedSubcategoryId)).isTrue()
    }

    @Test
    fun `existsBySubcategoryId returns false when no entry exists for subcategory`() {
        assertThat(adapter.existsBySubcategoryId(savedSubcategoryId)).isFalse()
    }

    // --- cascade: category ON DELETE CASCADE ---

    @Test
    fun `deleting category cascades and removes entries`() {
        val saved = adapter.save(income())

        categoryAdapter.deleteById(savedCategoryId)

        assertThat(adapter.findById(saved.id!!)).isNull()
    }

    // --- cascade: subcategory ON DELETE SET NULL ---

    @Test
    fun `deleting subcategory sets subcategoryId to null on existing entries`() {
        val saved = adapter.save(income(subcategoryId = savedSubcategoryId))

        subcategoryJpaRepository.deleteById(savedSubcategoryId)

        val reloaded = adapter.findById(saved.id!!)
        assertThat(reloaded).isNotNull()
        assertThat(reloaded!!.subcategoryId).isNull()
    }
}
