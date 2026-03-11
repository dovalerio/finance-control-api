/**
 * Schema layer — single entry point.
 *
 * Usage in specs and steps:
 *
 *   import { assertSchema, assertListSchema, categorySchema } from '../schemas'
 */

// Validation utilities
export { assertSchema, assertListSchema } from './validator'

// Domain schemas
export { errorSchema }                                  from './error.schema'
export { categorySchema, categoryListSchema }           from './category.schema'
export { subcategorySchema, subcategoryListSchema }     from './subcategory.schema'
export { transactionSchema, transactionListSchema }     from './transaction.schema'
export { balanceSchema, balanceWithCategorySchema }     from './balance.schema'
