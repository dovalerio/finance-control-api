import type {
  Subcategory,
  SubcategoryListParams,
  CreateSubcategoryPayload,
  RequestOverrides,
} from './types'

const BASE = '/subcategorias'

/**
 * HTTP client for /v1/subcategorias.
 *
 * Domain rules (desafio.pdf):
 *   - nome must be unique within the same id_categoria
 *   - cannot be deleted when it has linked transactions (returns 409)
 *   - deleted automatically when its parent category is removed
 */
export const subcategoryApi = {
  /**
   * GET /v1/subcategorias
   * Filterable by: nome, id_categoria
   */
  list(
    params?: SubcategoryListParams,
    overrides?: RequestOverrides,
  ): Cypress.Chainable<Cypress.Response<Subcategory[]>> {
    return cy.apiRequest<Subcategory[]>({
      method: 'GET',
      url: BASE,
      qs: { ...params, ...overrides?.qs },
      ...overrides,
    })
  },

  /**
   * GET /v1/subcategorias/{id_subcategoria}
   */
  getById(
    id: number,
    overrides?: RequestOverrides,
  ): Cypress.Chainable<Cypress.Response<Subcategory>> {
    return cy.apiRequest<Subcategory>({
      method: 'GET',
      url: `${BASE}/${id}`,
      ...overrides,
    })
  },

  /**
   * POST /v1/subcategorias
   * Required: nome, id_categoria
   */
  create(
    body: CreateSubcategoryPayload,
    overrides?: RequestOverrides,
  ): Cypress.Chainable<Cypress.Response<Subcategory>> {
    return cy.apiRequest<Subcategory>({
      method: 'POST',
      url: BASE,
      body,
      ...overrides,
    })
  },

  /**
   * PUT /v1/subcategorias/{id_subcategoria}
   */
  update(
    id: number,
    body: CreateSubcategoryPayload,
    overrides?: RequestOverrides,
  ): Cypress.Chainable<Cypress.Response<Subcategory>> {
    return cy.apiRequest<Subcategory>({
      method: 'PUT',
      url: `${BASE}/${id}`,
      body,
      ...overrides,
    })
  },

  /**
   * DELETE /v1/subcategorias/{id_subcategoria}
   * Returns 409 when subcategory has linked transactions.
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
