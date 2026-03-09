# Análise de Requisitos Pendentes

> Gerado em: 08/03/2026
> **Atualizado em: 09/03/2026**
> Base de análise: `Desafio - API Controle Financeiro.pdf`, `src/main/resources/static/api.yml`, código-fonte atual e testes existentes.

---

## Metodologia

Para cada item foi verificado:
- O contrato público definido em `api.yml`
- O comportamento real do código
- Os testes unitários (`src/test/`)
- Os testes E2E Cypress (`tests/cypress/e2e/`)

### Legenda de status

| Símbolo | Significado |
|---|---|
| ✅ **Resolvido** | Item corrigido e verificado |
| ⚠️ **Parcial** | Parcialmente resolvido — itens restantes detalhados |
| ❌ **Pendente** | Ainda não implementado |

---

## Itens resolvidos desde a última revisão

| ID | Título | Resolvido em |
|---|---|---|
| — | Bug: `BalanceResponse.category` com `@JsonIgnore` nunca serializado | 09/03/2026 |
| — | `api.yml`: campo `categoria` ausente no schema `Balanco` | 09/03/2026 |
| — | Unit test para `InvalidEntryAmountException` (422) | 09/03/2026 |
| — | Unit test para `SubcategoryHasEntriesException` (409) | 09/03/2026 |
| P3-002 (parcial) | Cypress: DELETE subcategoria com lançamentos → 409 | 09/03/2026 |
| P3-002 (parcial) | Cypress: balanço com `data_fim < data_inicio` → 400 | 09/03/2026 |
| P3-002 (parcial) | Cypress: `valor = 0` em lançamento → 422 | 09/03/2026 |
| P3-002 (parcial) | Cypress: subcategoria com nome duplicado na mesma categoria → 409 | 09/03/2026 |
| P3-002 (parcial) | Cypress: campo `categoria` presente no response de balanço filtrado | 09/03/2026 |

---

## P1 — Crítico (bugs que violam o contrato da API)

---

### P1-001 · ✅ `saldo` no balanço nunca é negativo — RESOLVIDO

**Arquivo:** [src/main/kotlin/com/heitor/finance/domain/valueobject/Money.kt](../src/main/kotlin/com/heitor/finance/domain/valueobject/Money.kt#L16)

**Problema:**

O operador `minus` de `Money` aplica `.max(BigDecimal.ZERO)`:

```kotlin
operator fun minus(other: Money): Money =
    Money(amount.subtract(other.amount).max(BigDecimal.ZERO))
```

Quando as despesas superam as receitas, o `saldo` retorna `0.00` em vez do valor negativo real.

**Efeito observável:**

```
GET /v1/balanco?data_inicio=2026-01-01&data_fim=2026-01-31
→ { "receita": 100.00, "despesa": 300.00, "saldo": 0.00  }   ← ERRADO
→ { "receita": 100.00, "despesa": 300.00, "saldo": -200.00 } ← CORRETO
```

**Causa:** `Money` é um value object não-negativo por design (representa valor monetário absoluto), mas `saldo` é um resultado de cálculo que pode ser negativo. Usar `Money.minus` para calcular o saldo é semanticamente incorreto.

**Correção necessária em** `FindBalanceUseCaseImpl.kt`:

```kotlin
// Sem filtro de categoria:
balance = revenue.amount.subtract(expense.amount)   // BigDecimal direto

// Com filtro de categoria:
balance = balance.income.amount.subtract(balance.expense.amount)
```

O `BalanceResponse.balance` deve ser `BigDecimal`, não `Money.amount`.

---

### P1-002 · ✅ `docker-compose.yml` não sobe a aplicação — RESOLVIDO

**Arquivo:** [docker-compose.yml](../docker-compose.yml)

**Problema:**

O `docker-compose.yml` define apenas o serviço `postgres`. O `dockerfile` existe e está funcional (multi-stage build, usuário não-root, Java 21), mas não há serviço `app` no compose.

**Requisito do desafio (Modernização):**

> "Uma boa sugestão é utilizar o Docker para buildar e rodar sua aplicação e provisionar outros serviços necessários (como o banco de dados por exemplo)."

**Estado atual:**

```bash
docker compose up -d
# sobe apenas o banco PostgreSQL na porta 5433
# a API precisa ser iniciada manualmente com ./gradlew bootRun
```

**Correção necessária:**

Adicionar serviço `app` ao `docker-compose.yml` referenciando o `dockerfile`, com as variáveis de ambiente necessárias (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `API_KEY`) e `depends_on: postgres`.

---

## P2 — Alto (desvios de design que afetam qualidade e manutenibilidade)

---

### P2-001 · ❌ `UpdateCategoryRequest` é classe morta

**Arquivo:** [src/main/kotlin/com/heitor/finance/application/dto/CategoryDto.kt](../src/main/kotlin/com/heitor/finance/application/dto/CategoryDto.kt#L11)

**Problema:**

`UpdateCategoryRequest` foi declarada mas nunca utilizada. O `CategoryController` usa `CreateCategoryRequest` tanto no `POST` quanto no `PUT`.

```kotlin
// declarada mas não referenciada em lugar nenhum
data class UpdateCategoryRequest(
    @JsonProperty("nome")
    @field:NotBlank(message = "O campo 'nome' é obrigatório")
    val name: String
)
```

**Impacto:** Dead code. Confunde quem lê os fontes. Viola princípios de código limpo.

**Correção:** Remover `UpdateCategoryRequest` de `CategoryDto.kt`.

---

### P2-002 · ❌ `CategoryApplicationService` quebra a convenção de nomenclatura

**Arquivo:** [src/main/kotlin/com/heitor/finance/application/service/CategoryApplicationService.kt](../src/main/kotlin/com/heitor/finance/application/service/CategoryApplicationService.kt)

**Problema:**

Todos os outros casos de uso seguem o padrão:
- Localização: `application/usecase/`
- Nomenclatura: `*UseCaseImpl`

```
application/usecase/CreateCategoryUseCaseImpl.kt
application/usecase/FindBalanceUseCaseImpl.kt
application/usecase/FindSubcategoryUseCaseImpl.kt
```

`CategoryApplicationService` está em `application/service/`, tem nome diferente e implementa apenas `FindCategoryUseCase`. A pasta `application/service/` não faz parte da arquitetura hexagonal declarada no projeto.

**Correção:**
- Renomear para `FindCategoryUseCaseImpl`
- Mover para `application/usecase/`
- Remover a pasta `application/service/` (ficará vazia)
- Atualizar `UseCaseConfig.kt` e `UseCaseConfigTest.kt`

---

### P2-003 · ❌ Coluna `comentario` NOT NULL no banco, campo opcional na spec

**Arquivo:** [src/main/resources/db/migration/V1__create_schema.sql](../src/main/resources/db/migration/V1__create_schema.sql#L18)

**Problema:**

```sql
comentario VARCHAR(255) NOT NULL DEFAULT ''
```

O campo é opcional na spec (`comentario` não consta em `required`). A implementação usa `DEFAULT ''` como sentinela e a resposta converte string vazia em `null` com `ifBlank { null }`.

Essa abordagem é tecnicamente funcional, mas semanticamente incorreta: o banco considera `''` (string vazia) e `null` como equivalentes, o que não é verdade.

**Correção necessária** (nova migration `V2__alter_lancamento_comentario.sql`):

```sql
ALTER TABLE lancamento ALTER COLUMN comentario DROP NOT NULL;
ALTER TABLE lancamento ALTER COLUMN comentario DROP DEFAULT;
UPDATE lancamento SET comentario = NULL WHERE comentario = '';
```

E em `EntryEntity.kt`:
```kotlin
@Column(name = "comentario", nullable = true)
val description: String?
```

---

### P2-004 · ❌ Colunas internas `tipo` e `id_categoria` redundantes na tabela `lancamento`

**Arquivo:** [src/main/resources/db/migration/V1__create_schema.sql](../src/main/resources/db/migration/V1__create_schema.sql#L21)

**Problema:**

A tabela `lancamento` contém:
- `tipo VARCHAR(20) NOT NULL` — armazena `INCOME` ou `EXPENSE`, derivado do sinal de `valor`
- `id_categoria BIGINT NOT NULL` — armazena a categoria pai, derivada da subcategoria

Nenhuma dessas colunas faz parte do contrato público (`api.yml`). São detalhes de implementação expostos no schema.

**Impacto:**

- `tipo` é redundante: pode sempre ser derivado de `valor > 0 = INCOME, valor < 0 = EXPENSE`
- `id_categoria` é redundante: pode ser recuperado via `JOIN subcategoria`
- Inconsistência teórica: se uma subcategoria mudar de categoria, o `id_categoria` no lançamento ficaria desatualizado

**Avaliação de risco:** Remoção é refatoração de impacto médio (domain model, entity, mappers, queries de balanço). Deve ser planejada como task separada.

---

### P2-005 · ❌ `CreateEntryRequest` sem `@NotNull` em campos obrigatórios

**Arquivo:** [src/main/kotlin/com/heitor/finance/application/dto/EntryDto.kt](../src/main/kotlin/com/heitor/finance/application/dto/EntryDto.kt#L13)

**Problema:**

```kotlin
data class CreateEntryRequest(
    @JsonProperty("valor") val value: BigDecimal,           // sem @field:NotNull
    @JsonProperty("id_subcategoria") val subcategoryId: Long, // sem @field:NotNull
    ...
)
```

Quando `valor` ou `id_subcategoria` estão ausentes no body, Jackson lança `MissingKotlinParameterException` (encapsulada em `HttpMessageNotReadableException`), retornando:

```json
{ "codigo": "corpo_invalido", "mensagem": "Malformed or missing request body" }
```

O contrato do desafio exige:

```json
{ "codigo": "erro_validacao", "mensagem": "O campo 'valor' é obrigatório" }
```

**Teste Cypress afetado:** `entries-create.cy.ts` verifica apenas `status 400`, não o `codigo`. O teste passa, mas o comportamento está fora do contrato.

**Correção:**

```kotlin
data class CreateEntryRequest(
    @JsonProperty("valor")
    @field:NotNull(message = "O campo 'valor' é obrigatório")
    val value: BigDecimal?,

    @JsonProperty("id_subcategoria")
    @field:NotNull(message = "O campo 'id_subcategoria' é obrigatório")
    val subcategoryId: Long?,
    ...
)
```

---

## P3 — Médio (cobertura de testes incompleta)

---

### P3-001 · ❌ Sem testes de integração com Testcontainers

**Dependências presentes em** [build.gradle.kts](../build.gradle.kts#L74):

```gradle
testImplementation("org.testcontainers:junit-jupiter:1.20.4")
testImplementation("org.testcontainers:postgresql:1.20.4")
```

**Problema:** Testcontainers está declarado nas dependências, mas não há nenhum teste que use um banco PostgreSQL real. Toda a cobertura é feita com mocks (MockK).

**O que não é testado com mocks:**
- Migrações Flyway
- Queries JPQL com filtros combinados (`findBySubcategoryIdAndPeriod`, `findByPeriodAndCategoryId`)
- Constraints de unicidade (`UNIQUE(nome)` em `categoria`, `UNIQUE(nome, id_categoria)` em `subcategoria`)
- Cascade `DELETE` de categoria → subcategoria → lançamento
- `ON DELETE SET NULL` em lançamento quando subcategoria é excluída

**Testes de integração necessários:**
- `CategoryRepositoryAdapterIT`
- `SubcategoryRepositoryAdapterIT`
- `EntryRepositoryAdapterIT`

---

### P3-002 · ⚠️ Cypress: casos de erro de negócio — parcialmente cobertos

**Resolvidos (09/03/2026):**

| Caso | Rota | HTTP | Arquivo |
|---|---|---|---|
| ✅ Excluir subcategoria com lançamentos | `DELETE /v1/subcategorias/{id}` | `409` | `subcategories-list.cy.ts` |
| ✅ Balanço com `data_inicio > data_fim` | `GET /v1/balanco` | `400` | `balance-calculation.cy.ts` |
| ✅ Criar lançamento com `valor = 0` | `POST /v1/lancamentos` | `422` | `entries-create.cy.ts` |
| ✅ Criar subcategoria com nome duplicado na mesma categoria | `POST /v1/subcategorias` | `409` | `subcategories-create.cy.ts` |
| ✅ Campo `categoria` no response de balanço filtrado | `GET /v1/balanco` | `200` | `balance-calculation.cy.ts` |

**Ainda pendentes:**

| Caso | Rota | HTTP esperado | Arquivo sugerido |
|---|---|---|---|
| ❌ Criar subcategoria com `id_categoria` inexistente | `POST /v1/subcategorias` | `404 Not Found` | `subcategories-create.cy.ts` |
| ❌ Criar lançamento com `id_subcategoria` inexistente | `POST /v1/lancamentos` | `404 Not Found` | `entries-create.cy.ts` |
| ❌ Atualizar categoria para nome já existente | `PUT /v1/categorias/{id}` | `409 Conflict` | `categories-list.cy.ts` |
| ❌ Atualizar subcategoria para nome já existente na mesma categoria | `PUT /v1/subcategorias/{id}` | `409 Conflict` | `subcategories-list.cy.ts` |

---

### P3-003 · ⚠️ Cypress: verificação do campo `codigo` — parcialmente adicionada

**Resolvido nos novos testes (09/03/2026):** Os cenários adicionados em P3-002 verificam explicitamente `response.body.codigo`.

**Ainda pendente nos testes existentes:**

| Arquivo | Teste | `codigo` verificado? |
|---|---|---|
| `categories-create.cy.ts` | `returns 400 when nome is missing` | ❌ |
| `categories-create.cy.ts` | `returns 409 when nome already exists` | ❌ |
| `categories-create.cy.ts` | `returns 401 when api-key header is absent` | ❌ |
| `categories-list.cy.ts` | `returns 404 for a non-existent id` | ❌ |
| `subcategories-create.cy.ts` | `returns 400 when nome is missing` | ❌ |
| `subcategories-create.cy.ts` | `returns 400 when id_categoria is missing` | ❌ |
| `subcategories-list.cy.ts` | `returns 404 for a non-existent id` | ❌ |
| `entries-create.cy.ts` | `returns 400 when valor is missing` | ❌ |
| `entries-create.cy.ts` | `returns 400 when id_subcategoria is missing` | ❌ |
| `entries-list.cy.ts` | `returns 404 for a non-existent id` | ❌ |
| `balance-calculation.cy.ts` | `returns 400 when data_inicio is missing` | ❌ |
| `balance-calculation.cy.ts` | `returns 400 when data_fim is missing` | ❌ |

---

## P4 — Baixo (melhorias de qualidade e documentação)

---

### P4-001 · ❌ `Entry.description` é semanticamente incorreto

**Arquivo:** [src/main/kotlin/com/heitor/finance/domain/model/Entry.kt](../src/main/kotlin/com/heitor/finance/domain/model/Entry.kt#L9)

O campo interno usa `description` mas o conceito no domínio é `comment` (mapeado para `comentario` na API). `description` é um nome mais genérico e pode confundir futuras manutenções.

**Correção:** Renomear `description` para `comment` no model `Entry`, `EntryEntity`, `EntryMapper`, e nos use cases que fazem referência ao campo.

---

### P4-002 · ❌ `GlobalExceptionHandler` não trata `MethodArgumentTypeMismatchException`

**Arquivo:** [src/main/kotlin/com/heitor/finance/infrastructure/exception/GlobalExceptionHandler.kt](../src/main/kotlin/com/heitor/finance/infrastructure/exception/GlobalExceptionHandler.kt)

**Problema:**

Se um parâmetro de tipo `Long` recebe um valor não numérico (ex: `/v1/categorias/abc`), Spring lança `MethodArgumentTypeMismatchException`. Esse tipo de erro não é tratado explicitamente e cai no handler genérico `Exception::class`, retornando `500 erro_interno`.

**Comportamento esperado:** `400 Bad Request` com `codigo: "parametro_invalido"`.

**Correção:**

```kotlin
@ExceptionHandler(MethodArgumentTypeMismatchException::class)
fun handleTypeMismatch(ex: MethodArgumentTypeMismatchException): ResponseEntity<ErrorResponse> {
    logger.warn("Type mismatch for parameter: {}", ex.name)
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
        ErrorResponse(codigo = "parametro_invalido", mensagem = "Parâmetro '${ex.name}' com formato inválido")
    )
}
```

---

### P4-003 · ❌ Métricas Prometheus sem autenticação

**Arquivo:** [src/main/resources/application.yaml](../src/main/resources/application.yaml#L26)

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,info
```

O endpoint `/actuator/metrics` (e `/actuator/prometheus`) está acessível sem `api-key`. O filtro `ApiKeyAuthFilter` protege apenas `/v1/*`.

**Avaliação:** Aceitável para ambiente de desenvolvimento, mas em produção as métricas devem ser restritas ou expostas em porta separada.

---

### P4-004 · ❌ Não há verificação de `jacoco` no Gradle com threshold de cobertura mínima

**Arquivo:** [build.gradle.kts](../build.gradle.kts#L95)

JaCoCo está configurado e gera relatórios, mas não há `jacocoTestCoverageVerification` que falhe o build caso a cobertura caia abaixo de um limiar.

**Recomendação:**

```kotlin
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
```

---

## Resumo executivo

| ID | Severidade | Título | Status |
|---|---|---|---|
| P1-001 | **Crítico** | `saldo` nunca negativo (`Money.minus` floored) | ✅ Resolvido |
| P1-002 | **Crítico** | Docker Compose sem serviço da aplicação | ✅ Resolvido |
| P2-001 | Alto | `UpdateCategoryRequest` classe morta | ❌ Pendente |
| P2-002 | Alto | `CategoryApplicationService` fora do padrão arquitetural | ❌ Pendente |
| P2-003 | Alto | `comentario` NOT NULL DEFAULT '' no banco | ❌ Pendente |
| P2-004 | Alto | Colunas internas `tipo` e `id_categoria` redundantes | ❌ Pendente |
| P2-005 | Alto | Campos obrigatórios sem `@NotNull` em `CreateEntryRequest` | ❌ Pendente |
| P3-001 | Médio | Sem testes de integração com Testcontainers | ❌ Pendente |
| P3-002 | Médio | Cypress sem casos de erro de negócio | ⚠️ Parcial (5 de 9 resolvidos) |
| P3-003 | Médio | Cypress não verifica `codigo` no corpo de erro | ⚠️ Parcial (novos testes OK, existentes não) |
| P4-001 | Baixo | `Entry.description` semanticamente incorreto | ❌ Pendente |
| P4-002 | Baixo | `MethodArgumentTypeMismatchException` retorna 500 | ❌ Pendente |
| P4-003 | Baixo | Métricas expostas sem autenticação | ❌ Pendente |
| P4-004 | Baixo | JaCoCo sem threshold mínimo configurado | ❌ Pendente |

---

## Prioridade de execução sugerida

```
1. P1-001 — Corrigir saldo negativo em FindBalanceUseCaseImpl      (< 30min — impacto isolado)
2. P1-002 — Adicionar serviço app no docker-compose.yml            (< 1h)
3. P2-005 — Adicionar @NotNull em CreateEntryRequest               (< 30min)
4. P2-001 — Remover UpdateCategoryRequest                          (< 15min)
5. P2-002 — Renomear CategoryApplicationService → FindCategoryUseCaseImpl  (< 30min)
6. P4-002 — Tratar MethodArgumentTypeMismatchException             (< 30min)
7. P3-002 — Adicionar 4 casos Cypress restantes                    (1–2h)
8. P3-003 — Adicionar verificação de codigo nos testes existentes  (1–2h)
9. P2-003 — Migration V2 para comentario nullable                  (1h)
10. P3-001 — Testes de integração com Testcontainers               (1 dia)
11. P2-004 — Remover tipo/id_categoria da tabela lancamento        (2 dias — refatoração maior)
```
