/**
 * API Client layer — single entry point.
 *
 * Usage in specs and steps:
 *
 *   import { categoryApi, subcategoryApi, transactionApi, balanceApi } from '../api'
 *
 * All clients use cy.apiRequest() which injects:
 *   - Content-Type: application/json
 *   - api-key: aXRhw7o=
 *
 * To test unauthenticated scenarios (401), use cy.request() directly
 * or pass overrides: { headers: { 'api-key': '' } }
 */
export { categoryApi } from './category.api'
export { subcategoryApi } from './subcategory.api'
export { transactionApi } from './transaction.api'
export { balanceApi } from './balance.api'

// Re-export all types so consumers only need one import path
export type {
  Category,
  Subcategory,
  Transaction,
  Balance,
  ApiError,
  CreateCategoryPayload,
  CreateSubcategoryPayload,
  CreateTransactionPayload,
  CategoryListParams,
  SubcategoryListParams,
  TransactionListParams,
  BalanceParams,
  RequestOverrides,
} from './types'
