package com.heitor.finance.infrastructure.persistence.adapter

import org.testcontainers.containers.PostgreSQLContainer

/**
 * Shared Testcontainers PostgreSQL instance.
 * Started once on first access; reused across all IT classes in this package.
 * Spring Boot caches the ApplicationContext because all IT classes register
 * the same datasource URL via @DynamicPropertySource.
 */
object TestDatabase {
    val container: PostgreSQLContainer<*> =
        PostgreSQLContainer("postgres:15-alpine").apply { start() }
}
