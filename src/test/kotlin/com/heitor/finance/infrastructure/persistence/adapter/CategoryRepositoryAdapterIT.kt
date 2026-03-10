package com.heitor.finance.infrastructure.persistence.adapter

import com.heitor.finance.domain.model.Category
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class CategoryRepositoryAdapterIT {

    companion object {
        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", TestDatabase.container::getJdbcUrl)
            registry.add("spring.datasource.username", TestDatabase.container::getUsername)
            registry.add("spring.datasource.password", TestDatabase.container::getPassword)
        }
    }

    @Autowired private lateinit var adapter: CategoryRepositoryAdapter
    @Autowired private lateinit var jdbcTemplate: JdbcTemplate

    @BeforeEach
    fun cleanup() {
        jdbcTemplate.execute("TRUNCATE TABLE lancamento, subcategoria, categoria RESTART IDENTITY CASCADE")
    }

    // --- Flyway ---

    @Test
    fun `flyway migrations are applied and schema exists`() {
        val count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM categoria", Int::class.java)
        assertThat(count).isEqualTo(0)
    }

    // --- save ---

    @Test
    fun `save returns category with generated id`() {
        val saved = adapter.save(Category(name = "Transport"))

        assertThat(saved.id).isNotNull()
        assertThat(saved.name).isEqualTo("Transport")
    }

    // --- findAll ---

    @Test
    fun `findAll without filter returns all categories`() {
        adapter.save(Category(name = "Transport"))
        adapter.save(Category(name = "Food"))

        val result = adapter.findAll(null)

        assertThat(result).hasSize(2)
        assertThat(result.map { it.name }).containsExactlyInAnyOrder("Transport", "Food")
    }

    @Test
    fun `findAll with name filter returns matching categories case insensitive`() {
        adapter.save(Category(name = "Transport"))
        adapter.save(Category(name = "Food"))

        val result = adapter.findAll("trans")

        assertThat(result).hasSize(1)
        assertThat(result.first().name).isEqualTo("Transport")
    }

    @Test
    fun `findAll with name filter returns empty list when no match`() {
        adapter.save(Category(name = "Food"))

        val result = adapter.findAll("xyz")

        assertThat(result).isEmpty()
    }

    // --- findById ---

    @Test
    fun `findById returns category when found`() {
        val saved = adapter.save(Category(name = "Transport"))

        val found = adapter.findById(saved.id!!)

        assertThat(found).isNotNull()
        assertThat(found!!.name).isEqualTo("Transport")
    }

    @Test
    fun `findById returns null when not found`() {
        assertThat(adapter.findById(999L)).isNull()
    }

    // --- update ---

    @Test
    fun `update changes category name`() {
        val saved = adapter.save(Category(name = "Transport"))

        val updated = adapter.update(Category(id = saved.id, name = "Travel"))

        assertThat(updated.name).isEqualTo("Travel")
        assertThat(adapter.findById(saved.id!!)!!.name).isEqualTo("Travel")
    }

    // --- deleteById ---

    @Test
    fun `deleteById removes category`() {
        val saved = adapter.save(Category(name = "Transport"))

        adapter.deleteById(saved.id!!)

        assertThat(adapter.findById(saved.id!!)).isNull()
    }

    // --- existsById ---

    @Test
    fun `existsById returns true when category exists`() {
        val saved = adapter.save(Category(name = "Transport"))

        assertThat(adapter.existsById(saved.id!!)).isTrue()
    }

    @Test
    fun `existsById returns false when category does not exist`() {
        assertThat(adapter.existsById(999L)).isFalse()
    }

    // --- existsByName ---

    @Test
    fun `existsByName returns true when name exists`() {
        adapter.save(Category(name = "Transport"))

        assertThat(adapter.existsByName("Transport")).isTrue()
    }

    @Test
    fun `existsByName returns false when name does not exist`() {
        assertThat(adapter.existsByName("Nonexistent")).isFalse()
    }

    // --- unique constraint ---

    @Test
    fun `save throws DataIntegrityViolationException on duplicate category name`() {
        adapter.save(Category(name = "Transport"))

        assertThatThrownBy { adapter.save(Category(name = "Transport")) }
            .isInstanceOf(DataIntegrityViolationException::class.java)
    }

    // --- cascade delete ---

    @Test
    fun `deleteById cascades and removes subcategories`() {
        val category = adapter.save(Category(name = "Transport"))
        jdbcTemplate.update(
            "INSERT INTO subcategoria (nome, id_categoria) VALUES (?, ?)",
            "Fuel", category.id
        )

        adapter.deleteById(category.id!!)

        val count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM subcategoria WHERE id_categoria = ?",
            Int::class.java, category.id
        )
        assertThat(count).isEqualTo(0)
    }

    @Test
    fun `deleteById cascades and removes entries`() {
        val category = adapter.save(Category(name = "Transport"))
        jdbcTemplate.update(
            "INSERT INTO lancamento (comentario, valor, tipo, data, id_categoria) VALUES (?, ?, ?, ?, ?)",
            "", java.math.BigDecimal("100.00"), "INCOME", java.time.LocalDate.of(2024, 3, 10), category.id
        )

        adapter.deleteById(category.id!!)

        val count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM lancamento WHERE id_categoria = ?",
            Int::class.java, category.id
        )
        assertThat(count).isEqualTo(0)
    }
}
