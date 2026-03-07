# GitHub Copilot Instructions

## Project Context

This project must strictly comply with the specification defined in the challenge document **"Desafio: API de Controle Financeiro"**.

All generated code must respect the contract defined in that document, even if the existing code currently differs.

---

# API Contract Alignment

The public API must follow the naming and structure defined in the challenge specification.

Expected routes:

- `/v1/categorias`
- `/v1/subcategorias`
- `/v1/lancamentos`
- `/v1/balanco`

Do **not expose English resource names** in the public API.

### Query parameters

The API must use the following parameters:

- `data_inicio`
- `data_fim`
- `id_categoria`
- `id_subcategoria`

### JSON field names

All response and request payloads must follow the specification naming:

- `id_categoria`
- `id_subcategoria`
- `id_lancamento`
- `nome`
- `valor`
- `data`
- `comentario`

Do not introduce alternative field names unless explicitly required.

### Date format

Dates must be compatible with the examples in the challenge specification.

---

# Error Response Contract

All API errors must follow the exact structure defined in the challenge.

Error response example:

```json
{
  "codigo": "erro_validacao",
  "mensagem": "O campo 'nome' é obrigatório"
}
````

Error responses must always include:

* HTTP status code
* `codigo`
* `mensagem`

Do **not return Spring `ProblemDetail`** or framework-specific error formats.

Error codes must be consistent and reusable.

---

# Required Versions

The project must strictly follow the versions defined in `build.gradle.kts`.

* Gradle: **9.3.1**
* Java: **21**
* Kotlin: **2.2.21**
* Spring Boot: **4.0.3**
* Spring Dependency Management Plugin: **1.1.7**

Do not suggest code incompatible with these versions.

---

# Official Sources First

Always prioritize:

1. Spring official documentation
2. Kotlin official documentation
3. Gradle official documentation
4. Official library documentation

Avoid outdated blogs or unofficial patterns.

---

# Language and Naming Rules

All source code artifacts must be written in **English**:

* class names
* interfaces
* method names
* variables
* file names
* enums
* packages
* test names

Do **not use Portuguese names in source code**.

Example naming:

* `CategoryEntity`
* `CreateCategoryUseCase`
* `BalanceController`
* `categoryRepository`
* `findByName`
* `application-prod.yaml`

Portuguese is only allowed in **API payload fields**, because they follow the challenge contract.

---

# Kotlin Rules

Prefer idiomatic Kotlin using features available in **Kotlin 2.2.21**.

### Prefer

* `data class`
* `sealed class` / `sealed interface`
* `@JvmInline value class`
* constructor injection
* `val` by default
* extension functions
* expression bodies
* `when` expressions
* Kotlin collection operators

### Avoid

* mutable state unless necessary
* `var` when not required
* Java-style boilerplate
* manual getters/setters
* `Optional`
* static utility classes
* `!!`
* confusing scope function chains
* excessive `lateinit`

---

# Kotlin Coding Conventions

Follow official Kotlin style guidelines:

* cohesive files
* clear package organization
* readable declarations
* small focused functions
* narrow visibility
* avoid abbreviations
* consistent structure

---

# Spring Rules

Prefer **native Spring Boot 4.0.3 / Spring Framework 7** features.

### Prefer native features

* `@RestController`
* `@RequestMapping`
* `@GetMapping`
* `@PostMapping`
* `@PutMapping`
* `@DeleteMapping`
* `@ConfigurationProperties`
* `@Validated`
* Jakarta Bean Validation
* Spring Data JPA
* Spring Actuator

Do not introduce libraries duplicating Spring functionality.

---

# Dependency Constraints

Use only dependencies defined in `build.gradle.kts`.

Current dependencies include:

* `spring-boot-starter-webmvc`
* `spring-boot-starter-data-jpa`
* `spring-boot-starter-validation`
* `spring-boot-starter-actuator`
* `kotlin-reflect`
* `jackson-module-kotlin`
* `postgresql`

Do not assume availability of:

* Spring Security
* WebFlux
* Redis
* Kafka
* MapStruct
* Lombok

unless explicitly added.

---

# Architecture Rules

The project follows **Hexagonal Architecture**.

### Layers

* `domain`
* `application`
* `infrastructure`
* `shared` (only if necessary)

### Domain

Contains:

* domain models
* value objects
* domain services
* business rules
* domain exceptions

Must not depend on Spring.

### Application

Contains:

* use cases
* input ports
* output ports
* orchestration services

### Infrastructure

Contains:

* controllers
* JPA entities
* repository adapters
* configuration
* filters
* external integrations

---

# Security Rule

All API requests must include the header:

```
api-key: aXRhw7o=
```

Requests without the correct header must return:

```
401 Unauthorized
```

Implement using **Spring `OncePerRequestFilter`**.

---

# Persistence Rules

Use:

* PostgreSQL
* Spring Data JPA

### Persistence separation

* JPA entities belong to infrastructure
* domain models must remain independent
* mapping must be done via mappers

---

# Database Migration Rules

All database changes must be versioned using **Flyway**.

Expected location:

```
src/main/resources/db/migration
```

Migrations must include:

* tables
* constraints
* foreign keys
* indexes
* unique constraints

Do not rely on Hibernate schema auto-generation.

---

# Validation Rules

Use **Jakarta Bean Validation**.

Validation responsibilities:

| Layer           | Responsibility     |
| --------------- | ------------------ |
| Transport layer | request validation |
| Domain layer    | business rules     |

Do not encode business rules only via annotations.

---

# Logging Rules

Use structured logging.

Example:

```java
logger.info("Creating category with name={}", name)
```

Never log:

* API keys
* passwords
* sensitive data

---

# Testing Rules

Generate tests using the current project stack.

### Unit tests

Prefer:

* JUnit 5
* Kotlin test
* MockK

### Test style

* English names
* descriptive tests
* Arrange / Act / Assert structure
* one behavior per test

Avoid fragile tests.

---

# End-to-End Testing (Cypress)

The repository contains a folder named:

```
tests/
```

located at the same level as `src`.

This folder contains **Cypress E2E tests** validating the API externally.

### Expected structure

```
tests
 ├─ cypress
 │   ├─ e2e
 │   ├─ fixtures
 │   ├─ support
 │   └─ utils
 ├─ cypress.config.ts
 └─ package.json
```

### Cypress conventions

* code written in English
* kebab-case filenames
* reusable commands
* no duplicated logic

Example test files:

* `categories-create.cy.ts`
* `categories-list.cy.ts`
* `entries-create.cy.ts`
* `balance-calculation.cy.ts`

### Test structure

```
Arrange
Act
Assert
```

### API Testing strategy

Cypress tests must validate:

* HTTP status codes
* response structure
* validation errors
* authentication via `api-key`
* edge cases

### Reusable commands

Define reusable commands in:

```
tests/cypress/support/commands.ts
```

Example helpers:

* `createCategory`
* `createSubcategory`
* `createEntry`

### Test coverage strategy

| Layer       | Tool        |
| ----------- | ----------- |
| Domain      | JUnit       |
| Use cases   | JUnit       |
| Controllers | Spring Test |
| Full API    | Cypress     |

Cypress tests must **not replace unit tests**.

---

# Docker Rules

Docker configuration must align with:

* Gradle 9.3.1
* Java 21
* Spring Boot 4.0.3

Prefer official images.

Avoid deprecated `openjdk`.

Use:

* multi-stage builds
* non-root runtime users
* minimal runtime images

---

# Documentation Rules

Documentation must always match the real implementation.

README must include:

* project description
* technology stack
* how to run the application
* API authentication
* Swagger/OpenAPI access
* example requests and responses

---

# Hard Constraints

Never generate:

* deprecated Spring APIs
* Java-style boilerplate instead of Kotlin
* Portuguese identifiers in source code
* incompatible dependency versions
* architecture mixing controller + business logic + persistence

---

# Output Quality Standard

All generated code must be:

* version compatible
* idiomatic Kotlin
* aligned with Spring Boot 4.0.3
* consistent with Gradle 9.3.1
* easy to test
* easy to maintain
* written in English

```

