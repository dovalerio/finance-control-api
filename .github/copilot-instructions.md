# GitHub Copilot Instructions

## Project Context

This project must strictly follow the versions and tooling defined in `build.gradle.kts`.

### Required Versions
- Gradle: 9.3.1
- Java: 21
- Kotlin: 2.2.21
- Spring Boot: 4.0.3
- Spring Dependency Management Plugin: 1.1.7

Do not suggest code, plugins, dependencies, syntax, or configurations incompatible with these versions.

## Official Sources First

Always prioritize:
1. Spring official documentation
2. Kotlin official documentation
3. Gradle official documentation
4. Official library documentation

Do not generate code based on outdated blog posts, deprecated APIs, or unofficial patterns when an official solution exists.

## Language and Naming Rules

All code artifacts must be written in English:
- class names
- interface names
- method names
- variable names
- file names
- enum names
- package names
- test names

Do not use Portuguese names in source code.

Examples:
- `CategoryEntity`
- `CreateCategoryUseCase`
- `BalanceController`
- `categoryRepository`
- `findByName`
- `application-prod.yaml`

## Kotlin Rules

Prefer idiomatic Kotlin and native Kotlin language features available in Kotlin 2.2.21.

### Prefer
- `data class` for immutable DTOs and simple domain models
- `sealed interface` or `sealed class` for controlled hierarchies
- `value class` with `@JvmInline` for lightweight typed wrappers when appropriate
- constructor injection
- nullable types only when truly necessary
- `val` by default
- extension functions for reusable transformations and utilities
- expression bodies when readability improves
- `when` instead of long `if/else` chains when applicable
- top-level functions for stateless helpers when that improves clarity
- Kotlin collection operators when they improve readability without hiding intent

### Avoid
- unnecessary mutable state
- `var` unless mutation is required
- Java-style boilerplate
- manual getters/setters without reason
- `Optional` in Kotlin code
- static utility classes
- broad use of `!!`
- meaningless scope function chains
- overuse of `lateinit`

### Scope Functions
Use Kotlin scope functions carefully and only when they improve clarity:
- `let` for nullable mapping or small transformations
- `run` for scoped computation
- `apply` for object setup
- `also` for side effects such as logging
- `with` only when it clearly improves readability

Do not chain scope functions in a confusing way.

## Kotlin Coding Conventions

Follow Kotlin official coding conventions:
- keep files cohesive
- use clear package organization
- keep declarations readable
- prefer small and focused functions
- keep visibility as narrow as possible
- avoid abbreviations
- organize code consistently

## Spring Rules

Prefer native Spring Boot 4.0.3 and Spring Framework 7 features before introducing extra libraries.

### Use Native Spring Features First
- `@RestController`
- `@RequestMapping`
- `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`
- `@ConfigurationProperties`
- `@Validated`
- Bean Validation with Jakarta Validation
- `ProblemDetail` for HTTP error responses when appropriate
- Spring Data JPA repositories
- Actuator for health and metrics
- Spring MVC features already included by the project

Do not introduce libraries that duplicate built-in Spring capabilities without a strong reason.

### Dependency Constraints
Only use dependencies already present in `build.gradle.kts`, unless a missing dependency is explicitly requested and justified.

Current project dependencies include:
- spring-boot-starter-webmvc
- spring-boot-starter-data-jpa
- spring-boot-starter-validation
- spring-boot-starter-actuator
- kotlin-reflect
- jackson-module-kotlin
- postgresql
- test dependencies already declared in the build

Do not assume Spring Security, WebFlux, Redis, Kafka, MapStruct, Lombok, or other libraries are available unless they are explicitly added to the build.

## Architecture Rules

The project should follow Hexagonal Architecture.

### Layers
- `domain`
- `application`
- `infrastructure`
- `shared` only when truly necessary

### Responsibilities

#### domain
Contains:
- domain models
- value objects
- domain services
- business rules
- domain exceptions

Must not depend on Spring annotations or framework types.

#### application
Contains:
- use cases
- input ports
- output ports
- orchestration services

Should coordinate business actions without leaking infrastructure concerns.

#### infrastructure
Contains:
- controllers
- JPA entities
- repository adapters
- external integrations
- configuration classes
- filters
- serializers

Framework-specific code belongs here.

## API Rules

Base path:
- `/v1`

Resource paths must use kebab-case and lowercase only.

Examples:
- `/v1/categories`
- `/v1/subcategories`
- `/v1/entries`
- `/v1/balance`

Use proper HTTP status codes.

## Security Rule

Every API request must require the header:
- `api-key`

Expected value:
- `aXRhw7o=`

Requests without the correct header must return:
- `401 Unauthorized`

Implement this with native Spring MVC filter infrastructure, preferably `OncePerRequestFilter`.

## Persistence Rules

Use PostgreSQL.
Use Spring Data JPA.
Use Kotlin JPA conventions compatible with the Kotlin Spring and JPA plugins already configured.

### Persistence Separation
- JPA entities belong to infrastructure
- domain models must not be JPA entities
- use mappers to convert between persistence models and domain/application models

### Migrations
If database migrations are added later, prefer Flyway.

## Validation Rules

Use Jakarta Bean Validation and Spring validation support.
Validation must happen at the correct boundary:
- request validation in transport layer
- business validation in domain/application layer

Do not put business rules only in annotations.

## Error Handling Rules

Prefer a centralized exception handling strategy.

Use consistent error responses.
When using Spring native features, prefer `ProblemDetail` and a global handler.

Do not return raw stack traces.
Do not expose internal exception messages directly to API consumers unless intentional and safe.

## Logging Rules

Use structured, concise, useful logs.

Prefer parameterized logging.

Example:
- `logger.info("Creating category with name={}", name)`

Do not log secrets, passwords, API keys, or sensitive personal data.

## Testing Rules

Generate tests with the existing project stack and Kotlin conventions.

### Unit Tests
Prefer:
- JUnit 5
- Kotlin test support
- Mocking only when necessary

### Test Style
- method names in English
- descriptive test names
- one behavior per test
- clear arrange / act / assert structure

Do not generate fragile tests.
Do not use integration test style when a unit test is enough.

## Docker Rules

Docker artifacts must be aligned with:
- Gradle 9.3.1
- Java 21
- Spring Boot 4.0.3

Prefer official Docker images.
Do not use deprecated `openjdk` images.

For Spring Boot containers:
- prefer multi-stage builds
- prefer non-root runtime user
- keep runtime image minimal
- keep image reproducible and readable

## Documentation Rules

Generated documentation must:
- be technically accurate
- reflect the actual build and dependency versions
- avoid inventing features not present in the project
- prefer concise, maintainable examples

When generating README, setup docs, or comments:
- use English in code examples
- use clear terminology
- avoid outdated commands

## Hard Constraints

Never generate:
- deprecated Spring APIs when current APIs exist
- Java boilerplate instead of idiomatic Kotlin
- Portuguese identifiers in source code
- incompatible dependency versions
- code that assumes libraries not present in `build.gradle.kts`
- architecture that mixes controller, business logic, and persistence in the same class

## Output Quality Standard

Every generated code suggestion must be:
- version-compatible
- idiomatic Kotlin
- aligned with Spring Boot 4.0.3
- consistent with Gradle 9.3.1
- easy to test
- easy to maintain
- written in English