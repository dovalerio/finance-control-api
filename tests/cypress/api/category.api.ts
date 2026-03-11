import type {
  Category,
  CategoryListParams,
  CreateCategoryPayload,
  RequestOverrides,
} from './types'

const BASE = '/categorias'

/**
 * HTTP client for /v1/categorias.
 *
 * All methods use cy.apiRequest(), which automatically injects:
 *   - Content-Type: application/json
 *   - api-key: aXRhw7o=
 *
 * To test 401 behaviour, pass overrides: { headers: { 'api-key': '' } }
 */
export const categoryApi = {
  /**
   * GET /v1/categorias
   * Filterable by: nome
   */
  list(
    params?: CategoryListParams,
    overrides?: RequestOverrides,
  ): Cypress.Chainable<Cypress.Response<Category[]>> {
    return cy.apiRequest<Category[]>({
      method: 'GET',
      url: BASE,
      qs: { ...params, ...overrides?.qs },
      ...overrides,
    })
  },

  /**
   * GET /v1/categorias/{id_categoria}
   */
  getById(
    id: number,
    overrides?: RequestOverrides,
  ): Cypress.Chainable<Cypress.Response<Category>> {
    return cy.apiRequest<Category>({
      method: 'GET',
      url: `${BASE}/${id}`,
      ...overrides,
    })
  },

  /**
   * POST /v1/categorias
   * Required: nome (unique in the system)
   */
  create(
    body: CreateCategoryPayload,
    overrides?: RequestOverrides,
  ): Cypress.Chainable<Cypress.Response<Category>> {
    return cy.apiRequest<Category>({
      method: 'POST',
      url: BASE,
      body,
      ...overrides,
    })
  },

  /**
   * PUT /v1/categorias/{id_categoria}
   */
  update(
    id: number,
    body: CreateCategoryPayload,
    overrides?: RequestOverrides,
  ): Cypress.Chainable<Cypress.Response<Category>> {
    return cy.apiRequest<Category>({
      method: 'PUT',
      url: `${BASE}/${id}`,
      body,
      ...overrides,
    })
  },

  /**
   * DELETE /v1/categorias/{id_categoria}
   * Cascades: removes all linked subcategories (and their transactions).
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
