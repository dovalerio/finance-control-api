type CreateCategoriaRequest = { nome: string }

type CreateSubcategoriaRequest = { nome: string; id_categoria: number }

type CreateLancamentoRequest = {
  valor: number
  id_subcategoria: number
  data?: string
  comentario?: string
}

type BalanceParams = {
  data_inicio: string
  data_fim: string
  id_categoria?: number
}

export const categoriesApi = {
  list: (params?: { nome?: string }) =>
    cy.apiRequest({ method: 'GET', url: '/categorias', qs: params }),

  getById: (id: number) =>
    cy.apiRequest({ method: 'GET', url: `/categorias/${id}` }),

  create: (body: CreateCategoriaRequest) =>
    cy.apiRequest({ method: 'POST', url: '/categorias', body }),

  update: (id: number, body: CreateCategoriaRequest) =>
    cy.apiRequest({ method: 'PUT', url: `/categorias/${id}`, body }),

  remove: (id: number) =>
    cy.apiRequest({ method: 'DELETE', url: `/categorias/${id}` }),
}

export const subcategoriesApi = {
  list: (params?: { nome?: string; id_categoria?: number }) =>
    cy.apiRequest({ method: 'GET', url: '/subcategorias', qs: params }),

  getById: (id: number) =>
    cy.apiRequest({ method: 'GET', url: `/subcategorias/${id}` }),

  create: (body: CreateSubcategoriaRequest) =>
    cy.apiRequest({ method: 'POST', url: '/subcategorias', body }),

  update: (id: number, body: CreateSubcategoriaRequest) =>
    cy.apiRequest({ method: 'PUT', url: `/subcategorias/${id}`, body }),

  remove: (id: number) =>
    cy.apiRequest({ method: 'DELETE', url: `/subcategorias/${id}` }),
}

export const entriesApi = {
  list: (params?: { id_subcategoria?: number; data_inicio?: string; data_fim?: string }) =>
    cy.apiRequest({ method: 'GET', url: '/lancamentos', qs: params }),

  getById: (id: number) =>
    cy.apiRequest({ method: 'GET', url: `/lancamentos/${id}` }),

  create: (body: CreateLancamentoRequest) =>
    cy.apiRequest({ method: 'POST', url: '/lancamentos', body }),

  update: (id: number, body: CreateLancamentoRequest) =>
    cy.apiRequest({ method: 'PUT', url: `/lancamentos/${id}`, body }),

  remove: (id: number) =>
    cy.apiRequest({ method: 'DELETE', url: `/lancamentos/${id}` }),
}

export const balanceApi = {
  calculate: (params: BalanceParams) =>
    cy.apiRequest({ method: 'GET', url: '/balanco', qs: params }),
}
