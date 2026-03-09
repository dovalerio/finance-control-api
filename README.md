# API de Controle Financeiro

Solução do desafio **"Desafio: API de Controle Financeiro"**.

API REST para controle de gastos e ganhos, permitindo o lançamento de receitas e despesas por subcategoria, consulta de lançamentos com filtros e cálculo de balanço por período.

---

## Sumário

- [Stack Tecnológica](#stack-tecnológica)
- [Arquitetura](#arquitetura)
- [Pré-requisitos](#pré-requisitos)
- [Executando Localmente](#executando-localmente)
- [Autenticação](#autenticação)
- [Referência da API](#referência-da-api)
- [Exemplos de Requisição](#exemplos-de-requisição)
- [Respostas de Erro](#respostas-de-erro)
- [Documentação Swagger](#documentação-swagger)
- [Testes](#testes)
- [Observabilidade](#observabilidade)
- [Logs](#logs)

---

## Stack Tecnológica

| Camada | Tecnologia |
|---|---|
| Linguagem | Kotlin 2.x |
| Framework | Spring Boot 4.0.3 |
| Build | Gradle (Kotlin DSL) |
| Runtime | Java 21 |
| Banco de Dados | PostgreSQL 16 |
| ORM | Spring Data JPA / Hibernate |
| Migrações | Flyway |
| Logging | Log4j2 |
| Documentação da API | Swagger UI (WebJar) |
| Containerização | Docker / Docker Compose |
| Testes Unitários | JUnit 5 + MockK |
| Testes E2E | Cypress 13 |

---

## Arquitetura

O projeto segue **Arquitetura Hexagonal (Ports and Adapters)**:

```
src/main/kotlin/com/heitor/finance/
├── domain/           # Entidades, value objects, serviços de domínio, exceções
│   ├── model/
│   ├── service/
│   ├── valueobject/
│   └── exception/
├── application/      # Casos de uso, ports de entrada/saída, DTOs
│   ├── usecase/
│   ├── port/
│   └── dto/
└── infrastructure/   # Controllers, entidades JPA, adapters, filtros, configuração
    ├── controller/
    ├── persistence/
    ├── filter/
    └── config/
```

- A camada **domain** não possui nenhuma dependência do Spring.
- As regras de negócio ficam nos **casos de uso**, não nos controllers.
- O contrato da API (`src/main/resources/static/api.yml`) é a fonte de verdade para rotas e campos públicos.

---

## Pré-requisitos

| Ferramenta | Versão |
|---|---|
| Java | 21+ |
| Docker | 24+ |
| Docker Compose | v2+ |
| Node.js | 18+ (somente para testes E2E com Cypress) |

---

## Executando Localmente

### 1. Subir o banco de dados

```bash
docker compose up -d
```

Sobe um container PostgreSQL 16 na porta **5433**.
As migrações Flyway são executadas automaticamente na inicialização da aplicação e criam todas as tabelas.

### 2. Iniciar a API

```bash
./gradlew bootRun
```

A API fica disponível em **http://localhost:8080**.

### Variáveis de ambiente

Todas possuem valores padrão e não precisam ser configuradas para rodar localmente.

| Variável | Padrão | Descrição |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5433/finance_db` | URL de conexão JDBC |
| `DB_USERNAME` | `postgres` | Usuário do banco de dados |
| `DB_PASSWORD` | `postgres` | Senha do banco de dados |
| `API_KEY` | `aXRhw7o=` | Chave exigida no header de todas as requisições |
| `APP_PORT` | `8080` | Porta HTTP da aplicação |

Para sobrescrever uma variável:

```bash
API_KEY=minhaChaveSecreta ./gradlew bootRun
```

### Comandos Docker úteis

```bash
# Subir o banco em segundo plano
docker compose up -d

# Parar e remover os containers
docker compose down

# Parar e remover containers + volumes (apaga os dados do banco)
docker compose down -v
```

---

## Autenticação

Toda requisição para `/v1/**` deve incluir o header:

```
api-key: aXRhw7o=
```

Chave ausente ou inválida retorna:

```json
HTTP 401 Unauthorized

{
  "codigo": "nao_autorizado",
  "mensagem": "Invalid or missing API key"
}
```

---

## Referência da API

URL base: `http://localhost:8080/v1`

### Categorias

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/v1/categorias` | Lista categorias (filtro opcional: `?nome=`) |
| `GET` | `/v1/categorias/{id_categoria}` | Busca categoria por ID |
| `POST` | `/v1/categorias` | Cria uma categoria |
| `PUT` | `/v1/categorias/{id_categoria}` | Atualiza uma categoria |
| `DELETE` | `/v1/categorias/{id_categoria}` | Remove uma categoria (cascata: remove subcategorias e lançamentos) |

**Campos:**

| Campo | Obrigatório? | Filtrável? | Descrição |
|---|---|---|---|
| `id_categoria` | Gerado automaticamente | Não | ID único, numérico e positivo |
| `nome` | Sim | Sim (`?nome=`) | Nome único no sistema |

---

### Subcategorias

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/v1/subcategorias` | Lista subcategorias (filtros opcionais: `?nome=`, `?id_categoria=`) |
| `GET` | `/v1/subcategorias/{id_subcategoria}` | Busca subcategoria por ID |
| `POST` | `/v1/subcategorias` | Cria uma subcategoria |
| `PUT` | `/v1/subcategorias/{id_subcategoria}` | Atualiza uma subcategoria |
| `DELETE` | `/v1/subcategorias/{id_subcategoria}` | Remove uma subcategoria (bloqueado se houver lançamentos vinculados) |

**Campos:**

| Campo | Obrigatório? | Filtrável? | Descrição |
|---|---|---|---|
| `id_subcategoria` | Gerado automaticamente | Sim (`?id_subcategoria=`) | ID único, numérico e positivo |
| `nome` | Sim | Sim (`?nome=`) | Nome único dentro de uma mesma categoria |
| `id_categoria` | Sim | Não | ID da categoria à qual pertence |

---

### Lançamentos

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/v1/lancamentos` | Lista lançamentos (filtros abaixo) |
| `GET` | `/v1/lancamentos/{id_lancamento}` | Busca lançamento por ID |
| `POST` | `/v1/lancamentos` | Cria um lançamento |
| `PUT` | `/v1/lancamentos/{id_lancamento}` | Atualiza um lançamento |
| `DELETE` | `/v1/lancamentos/{id_lancamento}` | Remove um lançamento |

**Campos:**

| Campo | Obrigatório? | Filtrável? | Descrição |
|---|---|---|---|
| `id_lancamento` | Gerado automaticamente | Não | ID único, numérico e positivo |
| `valor` | Sim | Não | Positivo = receita, negativo = despesa. Não pode ser zero |
| `data` | Padrão: data atual | Sim (`data_inicio` / `data_fim`) | Data do lançamento. Aceita apenas datas válidas |
| `id_subcategoria` | Sim | Sim (`?id_subcategoria=`) | ID da subcategoria do lançamento |
| `comentario` | Não | Não | Texto livre opcional |

**Filtros disponíveis para `GET /v1/lancamentos`:**

| Parâmetro | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `id_subcategoria` | inteiro | Não | Filtra por subcategoria |
| `data_inicio` | data (`YYYY-MM-DD`) | Não | Data inicial do período (inclusiva) |
| `data_fim` | data (`YYYY-MM-DD`) | Não | Data final do período (inclusiva) |

---

### Balanço

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/v1/balanco` | Calcula o balanço geral de um período |

O balanço é calculado em tempo real a partir dos lançamentos. Nada é gravado no banco.

**Parâmetros de consulta:**

| Parâmetro | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `data_inicio` | data (`YYYY-MM-DD`) | **Sim** | Data inicial do período (inclusiva) |
| `data_fim` | data (`YYYY-MM-DD`) | **Sim** | Data final do período (inclusiva) |
| `id_categoria` | inteiro | Não | Filtra o balanço por categoria |

**Campos retornados:**

| Campo | Descrição |
|---|---|
| `categoria` | Objeto com `id_categoria` e `nome` — retornado **apenas** quando filtrado por `id_categoria` |
| `receita` | Soma de todos os valores positivos no período |
| `despesa` | Soma de todos os valores negativos no período |
| `saldo` | `receita − despesa` |

---

## Exemplos de Requisição

### Categorias

**Criar**

```bash
curl -X POST http://localhost:8080/v1/categorias \
  -H "Content-Type: application/json" \
  -H "api-key: aXRhw7o=" \
  -d '{"nome": "Alimentação"}'
```

```json
HTTP 201 Created

{
  "id_categoria": 1,
  "nome": "Alimentação"
}
```

**Listar**

```bash
curl http://localhost:8080/v1/categorias \
  -H "api-key: aXRhw7o="
```

```json
HTTP 200 OK

[
  { "id_categoria": 1, "nome": "Alimentação" },
  { "id_categoria": 2, "nome": "Transporte" }
]
```

**Atualizar**

```bash
curl -X PUT http://localhost:8080/v1/categorias/1 \
  -H "Content-Type: application/json" \
  -H "api-key: aXRhw7o=" \
  -d '{"nome": "Alimentação & Bebidas"}'
```

```json
HTTP 200 OK

{
  "id_categoria": 1,
  "nome": "Alimentação & Bebidas"
}
```

**Remover**

```bash
curl -X DELETE http://localhost:8080/v1/categorias/1 \
  -H "api-key: aXRhw7o="
```

```
HTTP 204 No Content
```

---

### Subcategorias

**Criar**

```bash
curl -X POST http://localhost:8080/v1/subcategorias \
  -H "Content-Type: application/json" \
  -H "api-key: aXRhw7o=" \
  -d '{"nome": "Restaurantes", "id_categoria": 1}'
```

```json
HTTP 201 Created

{
  "id_subcategoria": 1,
  "nome": "Restaurantes",
  "id_categoria": 1
}
```

---

### Lançamentos

Convenção de sinal do campo `valor`:
- **Positivo** → receita (crédito)
- **Negativo** → despesa (débito)
- **Zero** → não permitido

**Criar lançamento de receita**

```bash
curl -X POST http://localhost:8080/v1/lancamentos \
  -H "Content-Type: application/json" \
  -H "api-key: aXRhw7o=" \
  -d '{
    "valor": 3500.00,
    "data": "2026-03-05",
    "id_subcategoria": 1,
    "comentario": "Salário"
  }'
```

```json
HTTP 201 Created

{
  "id_lancamento": 1,
  "valor": 3500.00,
  "data": "2026-03-05",
  "id_subcategoria": 1,
  "comentario": "Salário"
}
```

**Criar lançamento de despesa**

```bash
curl -X POST http://localhost:8080/v1/lancamentos \
  -H "Content-Type: application/json" \
  -H "api-key: aXRhw7o=" \
  -d '{
    "valor": -150.00,
    "data": "2026-03-07",
    "id_subcategoria": 2,
    "comentario": "Almoço"
  }'
```

```json
HTTP 201 Created

{
  "id_lancamento": 2,
  "valor": -150.00,
  "data": "2026-03-07",
  "id_subcategoria": 2,
  "comentario": "Almoço"
}
```

**Listar por período**

```bash
curl "http://localhost:8080/v1/lancamentos?data_inicio=2026-03-01&data_fim=2026-03-31" \
  -H "api-key: aXRhw7o="
```

```json
HTTP 200 OK

[
  { "id_lancamento": 1, "valor": 3500.00, "data": "2026-03-05", "id_subcategoria": 1, "comentario": "Salário" },
  { "id_lancamento": 2, "valor": -150.00, "data": "2026-03-07", "id_subcategoria": 2, "comentario": "Almoço" }
]
```

---

### Balanço

**Consultar balanço geral do período**

```bash
curl "http://localhost:8080/v1/balanco?data_inicio=2026-03-01&data_fim=2026-03-31" \
  -H "api-key: aXRhw7o="
```

```json
HTTP 200 OK

{
  "receita": 3500.00,
  "despesa": 150.00,
  "saldo": 3350.00
}
```

**Filtrar balanço por categoria**

```bash
curl "http://localhost:8080/v1/balanco?data_inicio=2026-03-01&data_fim=2026-03-31&id_categoria=1" \
  -H "api-key: aXRhw7o="
```

```json
HTTP 200 OK

{
  "categoria": {
    "id_categoria": 1,
    "nome": "Alimentação"
  },
  "receita": 0.00,
  "despesa": 150.00,
  "saldo": 0.00
}
```

---

## Respostas de Erro

Todos os erros seguem a mesma estrutura:

```json
{
  "codigo": "string",
  "mensagem": "string"
}
```

| Status HTTP | `codigo` | Situação |
|---|---|---|
| `400 Bad Request` | `erro_validacao` | Campo obrigatório ausente ou formato inválido |
| `400 Bad Request` | `periodo_invalido` | `data_inicio` posterior a `data_fim` |
| `400 Bad Request` | `parametro_ausente` | Parâmetro obrigatório ausente na query string |
| `400 Bad Request` | `corpo_invalido` | Corpo da requisição malformado ou ausente |
| `401 Unauthorized` | `nao_autorizado` | Header `api-key` ausente ou inválido |
| `404 Not Found` | `nao_encontrado` | Recurso não existe |
| `409 Conflict` | `conflito` | Nome já cadastrado, ou subcategoria com lançamentos vinculados |
| `422 Unprocessable Entity` | `valor_invalido` | Campo `valor` é zero |

**Exemplos:**

```json
HTTP 400 Bad Request

{
  "codigo": "erro_validacao",
  "mensagem": "O campo 'nome' é obrigatório"
}
```

```json
HTTP 409 Conflict

{
  "codigo": "conflito",
  "mensagem": "Subcategory id=3 has entries and cannot be deleted"
}
```

```json
HTTP 422 Unprocessable Entity

{
  "codigo": "valor_invalido",
  "mensagem": "Entry amount must not be zero"
}
```

---

## Documentação Swagger

A documentação interativa está disponível em:

```
http://localhost:8080/swagger-ui.html
```

O Swagger carrega o contrato OpenAPI exposto em `http://localhost:8080/api.yml`, que reflete exatamente as rotas e schemas definidos em `src/main/resources/static/api.yml`.

---

## Testes

### Testes unitários

```bash
./gradlew test
```

Gera relatório HTML de cobertura em `build/reports/tests/test/index.html`.

Cobertura mínima exigida pelo desafio: **90%** — cobertura atual: **~97%**.

Os testes utilizam **JUnit 5 + MockK** e não dependem de `@SpringBootTest` nem `@WebMvcTest`, garantindo isolamento total e execução rápida.

### Testes E2E (Cypress)

A API deve estar em execução antes de rodar os testes E2E.

```bash
# Instalar dependências (apenas na primeira vez)
cd tests && npm install

# Rodar todos os testes em modo headless
npx cypress run

# Abrir o runner interativo
npx cypress open
```

Os testes cobrem todos os fluxos principais:

- CRUD completo de categorias, subcategorias e lançamentos
- Cálculo de balanço por período e por categoria
- Autenticação — `401` sem `api-key`
- Validações — `400` para campos obrigatórios ausentes
- Regras de negócio — `valor = 0` (422), subcategoria com lançamentos (409), nomes duplicados (409), período inválido (400)

**Gerar relatório HTML (mochawesome):**

```bash
cd tests
npm run test:report
```

Relatórios gerados em `tests/reports/`:
- `detailed-report.html` — detalhamento por teste com requests/responses
- `summary-report.html` — resumo geral por spec

---

## Observabilidade

Endpoints do Spring Actuator disponíveis sem autenticação:

| Endpoint | Descrição |
|---|---|
| `GET /actuator/health` | Status de saúde da aplicação |
| `GET /actuator/metrics` | Métricas via Micrometer |
| `GET /actuator/info` | Informações da aplicação |

Métricas no formato Prometheus disponíveis em `/actuator/prometheus`.

---

## Logs

A aplicação utiliza **Log4j2** para logging estruturado em todos os casos relevantes:

| Nível | Quando |
|---|---|
| `INFO` | Criação, atualização e remoção de recursos |
| `WARN` | Erros de validação, regras de negócio violadas, tentativas de acesso não autorizado |
| `ERROR` | Exceções inesperadas |

Exemplo de rastreabilidade em caso de falha:

```
WARN  DeleteSubcategoryUseCaseImpl - Cannot delete subcategory id=3 — has associated entries
WARN  GlobalExceptionHandler - Subcategory delete blocked: Subcategory id=3 has entries and cannot be deleted
```
