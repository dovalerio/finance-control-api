# Finance Control API

API REST para controle de receitas e despesas.

A aplicação permite:

- Gerenciamento de categorias
- Gerenciamento de subcategorias
- Registro de lançamentos financeiros
- Consulta de balanço financeiro por período

## Tecnologias

- Kotlin 2.x
- Spring Boot 4.x
- Gradle
- PostgreSQL
- Spring Data JPA
- Spring Actuator
- Springdoc OpenAPI
- Docker
- Testcontainers
- JUnit 5
- MockK

## Arquitetura

A aplicação utiliza **Hexagonal Architecture (Ports and Adapters)**.

Objetivos:

- Isolamento do domínio
- Alta testabilidade
- Baixo acoplamento com frameworks

Estrutura principal:

```
domain
application
infrastructure
```

## Segurança

Todas as requisições exigem o header:

```
api-key: aXRhw7o=
```

Requisições sem esse header retornam `401 Unauthorized`.

## Endpoints

Prefixo da API: `/v1`

### Categorias

```
GET    /v1/categorias
GET    /v1/categorias/{id}
POST   /v1/categorias
PUT    /v1/categorias/{id}
DELETE /v1/categorias/{id}
```

### Subcategorias

```
GET  /v1/subcategorias
POST /v1/subcategorias
```

### Lançamentos

```
GET  /v1/lancamentos
POST /v1/lancamentos
```

### Balanço

```
GET /v1/balanco?data_inicio=&data_fim=&id_categoria=
```

Retorno:

```json
{
  "categoria": {
    "id_categoria": 1,
    "nome": "Transporte"
  },
  "receita": "2320.00",
  "despesa": "1000.00",
  "saldo": "1320.00"
}
```

## Executando localmente

Requisitos:

- Java 21
- Docker
- Gradle

Rodar aplicação:

```bash
./gradlew bootRun
```

### Docker

Build:

```bash
docker build -t finance-control-api .
```

Run:

```bash
docker-compose up
```

## Testes

Rodar testes:

```bash
./gradlew test
```

Cobertura mínima esperada: **90%**

## Observabilidade

Endpoints disponíveis via Spring Actuator:

- `/actuator/health`
- `/actuator/metrics`

## Documentação

Swagger disponível em: `/swagger-ui.html`
