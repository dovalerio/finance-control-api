package com.heitor.finance.infrastructure.persistence.adapter

import com.heitor.finance.domain.exception.CategoryNotFoundException
import com.heitor.finance.domain.model.Category
import com.heitor.finance.domain.model.Subcategory
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
class SubcategoryRepositoryAdapterIT {

    companion object {
        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", TestDatabase.container::getJdbcUrl)
            registry.add("spring.datasource.username", TestDatabase.container::getUsername)
            registry.add("spring.datasource.password", TestDatabase.container::getPassword)
        }
    }

    @Autowired private lateinit var adapter: SubcategoryRepositoryAdapter
    @Autowired private lateinit var categoryAdapter: CategoryRepositoryAdapter
    @Autowired private lateinit var jdbcTemplate: JdbcTemplate

    private lateinit var savedCategory: Category

    @BeforeEach
    fun setup() {
        jdbcTemplate.execute("TRUNCATE TABLE lancamento, subcategoria, categoria RESTART IDENTITY CASCADE")
        savedCategory = categoryAdapter.save(Category(name = "Transport"))
    }

    private fun sub(name: String, categoryId: Long = savedCategory.id!!) =
        Subcategory(name = name, categoryId = categoryId)

    // --- save ---

    @Test
    fun `save returns subcategory with generated id`() {
        val saved = adapter.save(sub("Fuel"))

        assertThat(saved.id).isNotNull()
        assertThat(saved.name).isEqualTo("Fuel")
        assertThat(saved.categoryId).isEqualTo(savedCategory.id)
    }

    @Test
    fun `save throws CategoryNotFoundException when category does not exist`() {
        assertThatThrownBy { adapter.save(sub("Fuel", categoryId = 999L)) }
            .isInstanceOf(CategoryNotFoundException::class.java)
    }

    // --- findAll ---

    @Test
    fun `findAll without filters returns all subcategories`() {
        adapter.save(sub("Fuel"))
        adapter.save(sub("Bus"))

        val result = adapter.findAll(null, null)

        assertThat(result).hasSize(2)
        assertThat(result.map { it.name }).containsExactlyInAnyOrder("Fuel", "Bus")
    }

    @Test
    fun `findAll with name filter returns matching subcategories case insensitive`() {
        adapter.save(sub("Fuel"))
        adapter.save(sub("Bus"))

        val result = adapter.findAll("uel", null)

        assertThat(result).hasSize(1)
        assertThat(result.first().name).isEqualTo("Fuel")
    }

    @Test
    fun `findAll with categoryId filter returns subcategories for that category`() {
        val other = categoryAdapter.save(Category(name = "Food"))
        adapter.save(sub("Fuel", savedCategory.id!!))
        adapter.save(sub("Meal", other.id!!))

        val result = adapter.findAll(null, savedCategory.id)

        assertThat(result).hasSize(1)
        assertThat(result.first().name).isEqualTo("Fuel")
    }

    @Test
    fun `findAll with name and categoryId returns matching subcategories for that category`() {
        val other = categoryAdapter.save(Category(name = "Food"))
        adapter.save(sub("Fuel", savedCategory.id!!))
        adapter.save(sub("Fuel", other.id!!))

        val result = adapter.findAll("Fuel", savedCategory.id)

        assertThat(result).hasSize(1)
        assertThat(result.first().categoryId).isEqualTo(savedCategory.id)
    }

    // --- findById ---

    @Test
    fun `findById returns subcategory when found`() {
        val saved = adapter.save(sub("Fuel"))

        val found = adapter.findById(saved.id!!)

        assertThat(found).isNotNull()
        assertThat(found!!.name).isEqualTo("Fuel")
    }

    @Test
    fun `findById returns null when not found`() {
        assertThat(adapter.findById(999L)).isNull()
    }

    // --- update ---

    @Test
    fun `update changes subcategory name`() {
        val saved = adapter.save(sub("Fuel"))

        val updated = adapter.update(Subcategory(id = saved.id, name = "Gasoline", categoryId = savedCategory.id!!))

        assertThat(updated.name).isEqualTo("Gasoline")
        assertThat(adapter.findById(saved.id!!)!!.name).isEqualTo("Gasoline")
    }

    // --- deleteById ---

    @Test
    fun `deleteById removes subcategory`() {
        val saved = adapter.save(sub("Fuel"))

        adapter.deleteById(saved.id!!)

        assertThat(adapter.findById(saved.id!!)).isNull()
    }

    // --- existsByNameInCategory ---

    @Test
    fun `existsByNameInCategory returns true when subcategory exists`() {
        adapter.save(sub("Fuel"))

        assertThat(adapter.existsByNameInCategory("Fuel", savedCategory.id!!)).isTrue()
    }

    @Test
    fun `existsByNameInCategory returns false when subcategory does not exist`() {
        assertThat(adapter.existsByNameInCategory("Fuel", savedCategory.id!!)).isFalse()
    }

    // --- unique constraint ---

    @Test
    fun `save throws DataIntegrityViolationException on duplicate name in same category`() {
        adapter.save(sub("Fuel"))

        assertThatThrownBy { adapter.save(sub("Fuel")) }
            .isInstanceOf(DataIntegrityViolationException::class.java)
    }

    @Test
    fun `same subcategory name in different categories is allowed`() {
        val other = categoryAdapter.save(Category(name = "Food"))
        adapter.save(sub("Common", savedCategory.id!!))

        val saved = adapter.save(sub("Common", other.id!!))

        assertThat(saved.id).isNotNull()
    }
}
