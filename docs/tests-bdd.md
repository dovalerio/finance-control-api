# Cenários de Teste — Formato BDD

Documentação de todos os cenários de teste da API de Controle Financeiro, escritos em formato Gherkin (Given / When / Then).

---

## Feature: Autenticação

```gherkin
Feature: Autenticação via api-key
  Como sistema protegido
  Quero rejeitar requisições sem credencial válida
  Para que apenas clientes autorizados acessem a API

  Scenario: Requisição sem header api-key
    Given que nenhum header api-key é enviado
    When faço GET /v1/categorias
    Then o status HTTP deve ser 401
    And o campo "codigo" deve ser "nao_autorizado"

  Scenario: Requisição com api-key inválida
    Given que o header api-key contém "chave_invalida"
    When faço GET /v1/categorias
    Then o status HTTP deve ser 401
    And o campo "codigo" deve ser "nao_autorizado"

  Scenario: Requisição com api-key válida
    Given que o header api-key contém "aXRhw7o="
    When faço GET /v1/categorias
    Then o status HTTP deve ser 200
```

---

## Feature: Categorias

```gherkin
Feature: Gerenciamento de Categorias
  Como usuário da API
  Quero criar e gerenciar categorias
  Para organizar meus lançamentos financeiros

  # --- Criação ---

  Scenario: Criar categoria com dados válidos
    Given que o header api-key é válido
    And o corpo da requisição contém {"nome": "Alimentação"}
    When faço POST /v1/categorias
    Then o status HTTP deve ser 201
    And a resposta deve conter o campo "id_categoria"
    And a resposta deve conter o campo "nome" com valor "Alimentação"

  Scenario: Criar categoria sem o campo nome
    Given que o header api-key é válido
    And o corpo da requisição é {}
    When faço POST /v1/categorias
    Then o status HTTP deve ser 400
    And o campo "codigo" deve ser "erro_validacao"
    And o campo "mensagem" deve mencionar "nome"

  Scenario: Criar categoria com nome duplicado
    Given que o header api-key é válido
    And já existe uma categoria com nome "Transporte"
    And o corpo da requisição contém {"nome": "Transporte"}
    When faço POST /v1/categorias
    Then o status HTTP deve ser 409
    And o campo "codigo" deve ser "conflito"

  # --- Consulta ---

  Scenario: Listar todas as categorias
    Given que o header api-key é válido
    When faço GET /v1/categorias
    Then o status HTTP deve ser 200
    And a resposta deve ser um array

  Scenario: Filtrar categorias por nome
    Given que o header api-key é válido
    And existe uma categoria com nome "Saúde"
    When faço GET /v1/categorias?nome=Saúde
    Then o status HTTP deve ser 200
    And o array deve conter ao menos um item com "nome" igual a "Saúde"

  Scenario: Buscar categoria por ID existente
    Given que o header api-key é válido
    And existe uma categoria com id_categoria 1
    When faço GET /v1/categorias/1
    Then o status HTTP deve ser 200
    And a resposta deve conter o campo "id_categoria" com valor 1

  Scenario: Buscar categoria com ID inexistente
    Given que o header api-key é válido
    When faço GET /v1/categorias/999999
    Then o status HTTP deve ser 404
    And o campo "codigo" deve ser "nao_encontrado"

  # --- Atualização ---

  Scenario: Atualizar categoria com dados válidos
    Given que o header api-key é válido
    And existe uma categoria com id_categoria 1
    And o corpo da requisição contém {"nome": "Alimentação e Bebidas"}
    When faço PUT /v1/categorias/1
    Then o status HTTP deve ser 200
    And a resposta deve conter "nome" com valor "Alimentação e Bebidas"

  Scenario: Atualizar categoria com ID inexistente
    Given que o header api-key é válido
    And o corpo da requisição contém {"nome": "Inexistente"}
    When faço PUT /v1/categorias/999999
    Then o status HTTP deve ser 404
    And o campo "codigo" deve ser "nao_encontrado"

  # --- Remoção ---

  Scenario: Remover categoria existente
    Given que o header api-key é válido
    And existe uma categoria com id_categoria 1 sem subcategorias
    When faço DELETE /v1/categorias/1
    Then o status HTTP deve ser 204

  Scenario: Remover categoria inexistente
    Given que o header api-key é válido
    When faço DELETE /v1/categorias/999999
    Then o status HTTP deve ser 404
    And o campo "codigo" deve ser "nao_encontrado"

  Scenario: Remover categoria com subcategorias (exclusão em cascata)
    Given que o header api-key é válido
    And existe uma categoria com id_categoria 1
    And existe uma subcategoria vinculada a essa categoria
    When faço DELETE /v1/categorias/1
    Then o status HTTP deve ser 204
    And ao buscar a subcategoria vinculada o status deve ser 404
```

---

## Feature: Subcategorias

```gherkin
Feature: Gerenciamento de Subcategorias
  Como usuário da API
  Quero criar e gerenciar subcategorias vinculadas a uma categoria
  Para classificar meus lançamentos com mais granularidade

  # --- Criação ---

  Scenario: Criar subcategoria com dados válidos
    Given que o header api-key é válido
    And existe uma categoria com id_categoria 1
    And o corpo contém {"nome": "Mercado", "id_categoria": 1}
    When faço POST /v1/subcategorias
    Then o status HTTP deve ser 201
    And a resposta deve conter "id_subcategoria"
    And a resposta deve conter "nome" com valor "Mercado"
    And a resposta deve conter "id_categoria" com valor 1

  Scenario: Criar subcategoria sem o campo nome
    Given que o header api-key é válido
    And o corpo contém {"id_categoria": 1}
    When faço POST /v1/subcategorias
    Then o status HTTP deve ser 400
    And o campo "codigo" deve ser "erro_validacao"
    And o campo "mensagem" deve mencionar "nome"

  Scenario: Criar subcategoria sem o campo id_categoria
    Given que o header api-key é válido
    And o corpo contém {"nome": "Farmácia"}
    When faço POST /v1/subcategorias
    Then o status HTTP deve ser 400
    And o campo "codigo" deve ser "erro_validacao"
    And o campo "mensagem" deve mencionar "id_categoria"

  Scenario: Criar subcategoria com id_categoria inexistente
    Given que o header api-key é válido
    And o corpo contém {"nome": "Gasolina", "id_categoria": 999999}
    When faço POST /v1/subcategorias
    Then o status HTTP deve ser 404
    And o campo "codigo" deve ser "nao_encontrado"

  Scenario: Criar subcategoria com nome duplicado dentro da mesma categoria
    Given que o header api-key é válido
    And existe uma subcategoria "Combustível" na categoria 1
    And o corpo contém {"nome": "Combustível", "id_categoria": 1}
    When faço POST /v1/subcategorias
    Then o status HTTP deve ser 409
    And o campo "codigo" deve ser "conflito"

  Scenario: Criar subcategoria com nome igual em categoria diferente (permitido)
    Given que o header api-key é válido
    And existe uma subcategoria "Combustível" na categoria 1
    And o corpo contém {"nome": "Combustível", "id_categoria": 2}
    When faço POST /v1/subcategorias
    Then o status HTTP deve ser 201

  # --- Consulta ---

  Scenario: Listar todas as subcategorias
    Given que o header api-key é válido
    When faço GET /v1/subcategorias
    Then o status HTTP deve ser 200
    And a resposta deve ser um array

  Scenario: Filtrar subcategorias por nome
    Given que o header api-key é válido
    And existe uma subcategoria com nome "Farmácia"
    When faço GET /v1/subcategorias?nome=Farmácia
    Then o status HTTP deve ser 200
    And o array deve conter ao menos um item com "nome" igual a "Farmácia"

  Scenario: Buscar subcategoria por ID existente
    Given que o header api-key é válido
    And existe uma subcategoria com id_subcategoria 1
    When faço GET /v1/subcategorias/1
    Then o status HTTP deve ser 200
    And a resposta deve conter "id_subcategoria", "nome" e "id_categoria"

  Scenario: Buscar subcategoria com ID inexistente
    Given que o header api-key é válido
    When faço GET /v1/subcategorias/999999
    Then o status HTTP deve ser 404
    And o campo "codigo" deve ser "nao_encontrado"

  # --- Atualização ---

  Scenario: Atualizar subcategoria com dados válidos
    Given que o header api-key é válido
    And existe uma subcategoria com id_subcategoria 1
    And o corpo contém {"nome": "Supermercado", "id_categoria": 1}
    When faço PUT /v1/subcategorias/1
    Then o status HTTP deve ser 200
    And a resposta deve conter "nome" com valor "Supermercado"

  Scenario: Atualizar subcategoria com ID inexistente
    Given que o header api-key é válido
    And o corpo contém {"nome": "Inexistente", "id_categoria": 1}
    When faço PUT /v1/subcategorias/999999
    Then o status HTTP deve ser 404

  # --- Remoção ---

  Scenario: Remover subcategoria sem lançamentos vinculados
    Given que o header api-key é válido
    And existe uma subcategoria com id_subcategoria 1 sem lançamentos
    When faço DELETE /v1/subcategorias/1
    Then o status HTTP deve ser 204

  Scenario: Remover subcategoria com lançamentos vinculados
    Given que o header api-key é válido
    And existe uma subcategoria com id_subcategoria 1
    And existem lançamentos vinculados a essa subcategoria
    When faço DELETE /v1/subcategorias/1
    Then o status HTTP deve ser 409
    And o campo "codigo" deve ser "conflito"

  Scenario: Remover subcategoria inexistente
    Given que o header api-key é válido
    When faço DELETE /v1/subcategorias/999999
    Then o status HTTP deve ser 404
    And o campo "codigo" deve ser "nao_encontrado"
```

---

## Feature: Lançamentos

```gherkin
Feature: Gerenciamento de Lançamentos
  Como usuário da API
  Quero registrar receitas e despesas por subcategoria
  Para acompanhar meu fluxo financeiro

  # --- Criação ---

  Scenario: Criar lançamento de receita (valor positivo)
    Given que o header api-key é válido
    And existe uma subcategoria com id_subcategoria 1
    And o corpo contém {"valor": 1500.00, "data": "2026-01-10", "id_subcategoria": 1}
    When faço POST /v1/lancamentos
    Then o status HTTP deve ser 201
    And a resposta deve conter "id_lancamento", "valor", "data", "id_subcategoria"
    And o campo "valor" deve ser 1500.00

  Scenario: Criar lançamento de despesa (valor negativo)
    Given que o header api-key é válido
    And existe uma subcategoria com id_subcategoria 1
    And o corpo contém {"valor": -300.00, "data": "2026-01-15", "id_subcategoria": 1}
    When faço POST /v1/lancamentos
    Then o status HTTP deve ser 201
    And o campo "valor" deve ser -300.00

  Scenario: Criar lançamento com comentário opcional
    Given que o header api-key é válido
    And existe uma subcategoria com id_subcategoria 1
    And o corpo contém {"valor": -2000.00, "data": "2026-01-20", "id_subcategoria": 1, "comentario": "Despesa extra"}
    When faço POST /v1/lancamentos
    Then o status HTTP deve ser 201
    And a resposta deve conter "comentario" com valor "Despesa extra"

  Scenario: Criar lançamento sem o campo data (usa data atual como padrão)
    Given que o header api-key é válido
    And existe uma subcategoria com id_subcategoria 1
    And o corpo contém {"valor": 100.00, "id_subcategoria": 1}
    When faço POST /v1/lancamentos
    Then o status HTTP deve ser 201
    And a resposta deve conter o campo "data" preenchido com a data atual

  Scenario: Criar lançamento com valor zero
    Given que o header api-key é válido
    And existe uma subcategoria com id_subcategoria 1
    And o corpo contém {"valor": 0, "data": "2026-01-01", "id_subcategoria": 1}
    When faço POST /v1/lancamentos
    Then o status HTTP deve ser 422
    And o campo "codigo" deve ser "valor_invalido"

  Scenario: Criar lançamento sem o campo valor
    Given que o header api-key é válido
    And o corpo contém {"id_subcategoria": 1}
    When faço POST /v1/lancamentos
    Then o status HTTP deve ser 400
    And o campo "codigo" deve ser "erro_validacao"
    And o campo "mensagem" deve mencionar "valor"

  Scenario: Criar lançamento sem o campo id_subcategoria
    Given que o header api-key é válido
    And o corpo contém {"valor": 100.00}
    When faço POST /v1/lancamentos
    Then o status HTTP deve ser 400
    And o campo "codigo" deve ser "erro_validacao"
    And o campo "mensagem" deve mencionar "id_subcategoria"

  Scenario: Criar lançamento com id_subcategoria inexistente
    Given que o header api-key é válido
    And o corpo contém {"valor": 100.00, "data": "2026-01-01", "id_subcategoria": 999999}
    When faço POST /v1/lancamentos
    Then o status HTTP deve ser 404
    And o campo "codigo" deve ser "nao_encontrado"

  # --- Consulta ---

  Scenario: Listar todos os lançamentos
    Given que o header api-key é válido
    When faço GET /v1/lancamentos
    Then o status HTTP deve ser 200
    And a resposta deve ser um array

  Scenario: Filtrar lançamentos por id_subcategoria
    Given que o header api-key é válido
    And existe um lançamento vinculado à subcategoria 1
    When faço GET /v1/lancamentos?id_subcategoria=1
    Then o status HTTP deve ser 200
    And todos os itens do array devem ter "id_subcategoria" igual a 1

  Scenario: Filtrar lançamentos por período
    Given que o header api-key é válido
    When faço GET /v1/lancamentos?data_inicio=2026-01-01&data_fim=2026-01-31
    Then o status HTTP deve ser 200
    And todos os itens do array devem ter "data" entre 2026-01-01 e 2026-01-31

  Scenario: Buscar lançamento por ID existente
    Given que o header api-key é válido
    And existe um lançamento com id_lancamento 1
    When faço GET /v1/lancamentos/1
    Then o status HTTP deve ser 200
    And a resposta deve conter "id_lancamento" com valor 1

  Scenario: Buscar lançamento com ID inexistente
    Given que o header api-key é válido
    When faço GET /v1/lancamentos/999999
    Then o status HTTP deve ser 404
    And o campo "codigo" deve ser "nao_encontrado"

  # --- Atualização ---

  Scenario: Atualizar lançamento com dados válidos
    Given que o header api-key é válido
    And existe um lançamento com id_lancamento 1
    And o corpo contém {"valor": 1800.00, "data": "2026-01-10", "id_subcategoria": 1}
    When faço PUT /v1/lancamentos/1
    Then o status HTTP deve ser 200
    And o campo "valor" deve ser 1800.00

  Scenario: Atualizar lançamento com ID inexistente
    Given que o header api-key é válido
    And o corpo contém {"valor": 100.00, "id_subcategoria": 1}
    When faço PUT /v1/lancamentos/999999
    Then o status HTTP deve ser 404

  # --- Remoção ---

  Scenario: Remover lançamento existente
    Given que o header api-key é válido
    And existe um lançamento com id_lancamento 1
    When faço DELETE /v1/lancamentos/1
    Then o status HTTP deve ser 204

  Scenario: Remover lançamento inexistente
    Given que o header api-key é válido
    When faço DELETE /v1/lancamentos/999999
    Then o status HTTP deve ser 404
    And o campo "codigo" deve ser "nao_encontrado"
```

---

## Feature: Balanço

```gherkin
Feature: Consulta de Balanço Geral
  Como usuário da API
  Quero consultar o balanço financeiro de um período
  Para entender minha situação financeira

  Background:
    Given que o header api-key é válido
    And existem os seguintes lançamentos no período 2026-01-01 a 2026-01-31:
      | valor    | tipo    |
      | 1800.00  | receita |
      | -300.00  | despesa |
      | -2000.00 | despesa |

  Scenario: Calcular balanço por período sem filtro de categoria
    When faço GET /v1/balanco?data_inicio=2026-01-01&data_fim=2026-01-31
    Then o status HTTP deve ser 200
    And a resposta deve conter "receita", "despesa" e "saldo"
    And o campo "receita" deve ser 1800.00
    And o campo "despesa" deve ser 2300.00
    And o campo "saldo" deve ser -500.00
    And a resposta NÃO deve conter o campo "categoria"

  Scenario: Balanço com saldo negativo (despesas superam receitas)
    When faço GET /v1/balanco?data_inicio=2026-01-01&data_fim=2026-01-31
    Then o status HTTP deve ser 200
    And o campo "saldo" deve ser menor que zero

  Scenario: Calcular balanço filtrado por categoria
    Given que os lançamentos pertencem à categoria 1
    When faço GET /v1/balanco?data_inicio=2026-01-01&data_fim=2026-01-31&id_categoria=1
    Then o status HTTP deve ser 200
    And a resposta deve conter o objeto "categoria"
    And "categoria.id_categoria" deve ser 1
    And a resposta deve conter "receita", "despesa" e "saldo"

  Scenario: data_inicio posterior a data_fim
    When faço GET /v1/balanco?data_inicio=2026-02-01&data_fim=2026-01-01
    Then o status HTTP deve ser 400
    And o campo "codigo" deve ser "periodo_invalido"

  Scenario: Ausência de data_inicio
    When faço GET /v1/balanco?data_fim=2026-01-31
    Then o status HTTP deve ser 400
    And o campo "codigo" deve ser "parametro_ausente"

  Scenario: Ausência de data_fim
    When faço GET /v1/balanco?data_inicio=2026-01-01
    Then o status HTTP deve ser 400
    And o campo "codigo" deve ser "parametro_ausente"

  Scenario: Balanço sem autenticação
    Given que nenhum header api-key é enviado
    When faço GET /v1/balanco?data_inicio=2026-01-01&data_fim=2026-01-31
    Then o status HTTP deve ser 401
    And o campo "codigo" deve ser "nao_autorizado"
```

---

## Feature: Regras de Negócio — Exclusão

```gherkin
Feature: Regras de proteção na exclusão de recursos
  Como sistema consistente
  Quero garantir integridade referencial nas exclusões
  Para não deixar dados órfãos

  Scenario: Excluir subcategoria com lançamentos vinculados (bloqueado)
    Given que o header api-key é válido
    And existe uma subcategoria com id_subcategoria 1
    And existem lançamentos vinculados a essa subcategoria
    When faço DELETE /v1/subcategorias/1
    Then o status HTTP deve ser 409
    And o campo "codigo" deve ser "conflito"
    And os lançamentos vinculados devem permanecer no sistema

  Scenario: Excluir subcategoria após remover seus lançamentos (permitido)
    Given que o header api-key é válido
    And os lançamentos da subcategoria 1 foram removidos
    When faço DELETE /v1/subcategorias/1
    Then o status HTTP deve ser 204

  Scenario: Excluir categoria remove suas subcategorias em cascata
    Given que o header api-key é válido
    And existe uma categoria com id_categoria 10
    And existe uma subcategoria com id_subcategoria 20 vinculada à categoria 10
    And a subcategoria 20 não possui lançamentos
    When faço DELETE /v1/categorias/10
    Then o status HTTP deve ser 204
    And ao buscar GET /v1/subcategorias/20 o status deve ser 404
```
