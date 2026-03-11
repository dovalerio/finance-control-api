// ─────────────────────────────────────────────────────────────────────────────
// Response shapes — aligned to desafio.pdf contract
// ─────────────────────────────────────────────────────────────────────────────

export interface Category {
  id_categoria: number
  nome: string
}

export interface Subcategory {
  id_subcategoria: number
  nome: string
  id_categoria: number
}

export interface Transaction {
  id_lancamento: number
  /** Positive = income (receita), negative = expense (despesa) */
  valor: number
  /** Brazilian date format: dd/MM/yyyy */
  data: string
  id_subcategoria: number
  comentario: string
}

export interface Balance {
  /** String decimal per spec: "2320.00" */
  receita: string
  despesa: string
  saldo: string
  /** Present only when filtered by id_categoria */
  categoria?: Pick<Category, 'id_categoria' | 'nome'>
}

export interface ApiError {
  codigo: string
  mensagem: string
}

// ─────────────────────────────────────────────────────────────────────────────
// Request payload types
// ─────────────────────────────────────────────────────────────────────────────

export type CreateCategoryPayload = {
  nome: string
}

export type CreateSubcategoryPayload = {
  nome: string
  id_categoria: number
}

export type CreateTransactionPayload = {
  valor: number
  id_subcategoria: number
  /** Optional — defaults to current date when omitted. Format: dd/MM/yyyy */
  data?: string
  comentario?: string
}

// ─────────────────────────────────────────────────────────────────────────────
// Query param types
// ─────────────────────────────────────────────────────────────────────────────

export type CategoryListParams = {
  nome?: string
}

export type SubcategoryListParams = {
  nome?: string
  id_categoria?: number
}

export type TransactionListParams = {
  id_subcategoria?: number
  /** Format: dd/MM/yyyy */
  data_inicio?: string
  /** Format: dd/MM/yyyy */
  data_fim?: string
}

export type BalanceParams = {
  /** Required. Format: dd/MM/yyyy */
  data_inicio: string
  /** Required. Format: dd/MM/yyyy */
  data_fim: string
  id_categoria?: number
}

// ─────────────────────────────────────────────────────────────────────────────
// Shared override type — passed to every API client method
// Allows tests to override headers (e.g. remove api-key for 401 scenarios)
// or inject extra query params without changing the method signature.
// ─────────────────────────────────────────────────────────────────────────────

export type RequestOverrides = Partial<
  Pick<Cypress.RequestOptions, 'headers' | 'qs' | 'failOnStatusCode' | 'timeout'>
>
