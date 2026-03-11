plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.spring") version "2.2.21"
    kotlin("plugin.jpa") version "2.2.21"
    jacoco

    id("org.springframework.boot") version "3.5.3"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.heitor"
version = "0.0.1-SNAPSHOT"
description = "Finance Control API"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

// Spring Boot 3.5.3 BOM upgrades Testcontainers to 1.21.2, which has a Docker Desktop 4.x
// detection regression on Windows. Override the BOM-managed property to pin to 1.20.4.
extra["testcontainers.version"] = "1.20.4"

// Exclui Logback (default do Spring Boot) para usar Log4j2
configurations.all {
    exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    exclude(group = "ch.qos.logback", module = "logback-classic")
    exclude(group = "ch.qos.logback", module = "logback-core")
}

dependencies {

    // Logging — Log4j2 2.24.3 (gerenciado pelo Spring Boot BOM; sem CVEs conhecidos)
    implementation("org.springframework.boot:spring-boot-starter-log4j2")

    // Spring
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Jackson Kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // OpenAPI / Swagger UI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")

    // Database
    runtimeOnly("org.postgresql:postgresql")

    // Flyway migrations
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")

    // Observability (Micrometer already comes with Actuator)
    implementation("io.micrometer:micrometer-registry-prometheus")

    // Hot reload — excluído automaticamente do bootJar (não vai para produção)
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // --- TESTS ---

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // Kotlin test
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

    // Mocking
    testImplementation("io.mockk:mockk:1.13.12")

    // Testcontainers
    testImplementation("org.testcontainers:junit-jupiter:1.20.4")
    testImplementation("org.testcontainers:postgresql:1.20.4")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xjsr305=strict",
            "-Xannotation-default-target=param-property"
        )
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

jacoco {
    toolVersion = "0.8.11"
}

tasks.bootRun {
    // Ativa o profile de desenvolvimento e habilita DevTools restart
    args("--spring.profiles.active=dev")
    jvmArgs(
        "-Dspring.devtools.restart.enabled=true",
        "-Dspring.devtools.livereload.enabled=true"
    )
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)

    // Docker Desktop 4.34+ on Windows rejects API requests with version < 1.45.
    // docker-java (used by Testcontainers 1.20.4) defaults to API v1.41, which causes
    // a 400 Bad Request for every endpoint, making Testcontainers fail to detect Docker.
    // Solution: set DOCKER_API_VERSION >= 1.45 and use the TCP endpoint exposed by Desktop's
    // "Expose daemon on tcp://localhost:2375 without TLS" setting (named pipes require
    // Desktop auth tokens that docker-java does not provide).
    // systemProperty is used to guarantee propagation to the forked test worker JVM.
    if (System.getProperty("os.name")?.contains("Windows", ignoreCase = true) == true) {
        val dockerHost = System.getenv("DOCKER_HOST") ?: "tcp://localhost:2375"
        environment("DOCKER_HOST", dockerHost)
        systemProperty("DOCKER_HOST", dockerHost)
        // docker-java reads the API version from the "api.version" system property
        // (or from DOCKER_API_VERSION env var). The system property name is different
        // from the environment variable name.
        systemProperty("api.version", "1.45")
        environment("DOCKER_API_VERSION", "1.45")
    }

    jvmArgs(
        "-XX:+EnableDynamicAgentLoading", // suppresses MockK/ByteBuddy dynamic agent warning
        "-Xshare:off"                      // suppresses JaCoCo CDS classpath-sharing warning
    )
    testLogging {
        events("failed", "skipped")
        showStandardStreams = false
        showExceptions = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.SHORT
    }
    addTestListener(object : TestListener {
        override fun beforeSuite(suite: TestDescriptor) {}
        override fun beforeTest(testDescriptor: TestDescriptor) {}
        override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {}
        override fun afterSuite(suite: TestDescriptor, result: TestResult) {
            if (suite.parent == null) {
                println(
                    "\nTests run: ${result.testCount}  " +
                    "Passed: ${result.successfulTestCount}  " +
                    "Failed: ${result.failedTestCount}  " +
                    "Skipped: ${result.skippedTestCount}"
                )
            }
        }
    })
}
tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude("**/*IT.*", "**/*IntegrationTest.*")
            }
        })
    )
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.90".toBigDecimal()
            }
        }
    }
}
tasks.check { dependsOn(tasks.jacocoTestCoverageVerification) }

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}