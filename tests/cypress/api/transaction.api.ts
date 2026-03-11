import type {
  Transaction,
  TransactionListParams,
  CreateTransactionPayload,
  RequestOverrides,
} from './types'

const BASE = '/lancamentos'

/**
 * HTTP client for /v1/lancamentos.
 *
 * Domain rules (desafio.pdf):
 *   - valor must be non-zero (422 with codigo: "valor_invalido" otherwise)
 *   - positive valor = income (receita); negative = expense (despesa)
 *   - data is optional — defaults to current system date when omitted
 *   - data format: dd/MM/yyyy  (NOT ISO 8601)
 *   - id_subcategoria must reference an existing subcategory
 */
export const transactionApi = {
  /**
   * GET /v1/lancamentos
   * Filterable by: id_subcategoria, data_inicio, data_fim (format: dd/MM/yyyy)
   */
  list(
    params?: TransactionListParams,
    overrides?: RequestOverrides,
  ): Cypress.Chainable<Cypress.Response<Transaction[]>> {
    return cy.apiRequest<Transaction[]>({
      method: 'GET',
      url: BASE,
      qs: { ...params, ...overrides?.qs },
      ...overrides,
    })
  },

  /**
   * GET /v1/lancamentos/{id_lancamento}
   */
  getById(
    id: number,
    overrides?: RequestOverrides,
  ): Cypress.Chainable<Cypress.Response<Transaction>> {
    return cy.apiRequest<Transaction>({
      method: 'GET',
      url: `${BASE}/${id}`,
      ...overrides,
    })
  },

  /**
   * POST /v1/lancamentos
   * Required: valor (≠ 0), id_subcategoria
   * Optional: data (dd/MM/yyyy), comentario
   */
  create(
    body: CreateTransactionPayload,
    overrides?: RequestOverrides,
  ): Cypress.Chainable<Cypress.Response<Transaction>> {
    return cy.apiRequest<Transaction>({
      method: 'POST',
      url: BASE,
      body,
      ...overrides,
    })
  },

  /**
   * PUT /v1/lancamentos/{id_lancamento}
   */
  update(
    id: number,
    body: CreateTransactionPayload,
    overrides?: RequestOverrides,
  ): Cypress.Chainable<Cypress.Response<Transaction>> {
    return cy.apiRequest<Transaction>({
      method: 'PUT',
      url: `${BASE}/${id}`,
      body,
      ...overrides,
    })
  },

  /**
   * DELETE /v1/lancamentos/{id_lancamento}
   */
  remove(
    id: number,
    overrides?: RequestOverrides,
  ): Cypress.Chainable<Cypress.Response<null>> {
    return cy.apiRequest<null>({
      method: 'DELETE',
      url: `${BASE}/${id}`,
      ...overrides,
    })
  },
}
