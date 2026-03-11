import type { Balance, BalanceParams, RequestOverrides } from './types'

const BASE = '/balanco'

/**
 * HTTP client for /v1/balanco.
 *
 * This resource is read-only (GET only). No CRUD operations.
 *
 * Domain rules (desafio.pdf):
 *   - data_inicio and data_fim are required
 *   - data_inicio must be ≤ data_fim (400 with codigo: "periodo_invalido" otherwise)
 *   - id_categoria is optional — when provided, response includes "categoria" object
 *   - balance is calculated in real-time; nothing is persisted
 *   - date format: dd/MM/yyyy
 */
export const balanceApi = {
  /**
   * GET /v1/balanco?data_inicio=...&data_fim=...&id_categoria=...
   *
   * @param params - data_inicio and data_fim are required
   * @param overrides - optional header/qs overrides (e.g. for 401 tests)
   */
  calculate(
    params: BalanceParams,
    overrides?: RequestOverrides,
  ): Cypress.Chainable<Cypress.Response<Balance>> {
    return cy.apiRequest<Balance>({
      method: 'GET',
      url: BASE,
      qs: { ...params, ...overrides?.qs },
      ...overrides,
    })
  },
}
