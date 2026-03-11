/**
 * JSON Schema for the Transaction (Lançamento) resource.
 *
 * Actual API response (confirmed):
 *   {
 *     "id_lancamento": 55,
 *     "valor": 123.45,
 *     "data": "10/03/2026",
 *     "id_subcategoria": 90,
 *     "comentario": "Schema check"
 *   }
 *
 * Domain rules (desafio.pdf):
 *   - valor != 0  (enforced at write time; response never contains 0)
 *   - valor > 0   → income (receita)
 *   - valor < 0   → expense (despesa)
 *   - data default → current system date when not provided at creation
 *   - comentario is optional at creation but always present in the response
 */
export const transactionSchema = {
  type: 'object',
  required: ['id_lancamento', 'valor', 'data', 'id_subcategoria', 'comentario'],
  additionalProperties: false,
  properties: {
    id_lancamento: {
      type: 'integer',
      minimum: 1,
      description: 'Auto-generated unique positive identifier',
    },
    valor: {
      type: 'number',
      description: 'Monetary value — positive (income) or negative (expense), never zero',
    },
    data: {
      type: 'string',
      // Enforces the Brazilian date format required by the API: dd/MM/yyyy
      // e.g. "10/03/2026" — NOT ISO 8601 "2026-03-10"
      pattern: '^\\d{2}/\\d{2}/\\d{4}$',
      description: 'Transaction date in dd/MM/yyyy format',
    },
    id_subcategoria: {
      type: 'integer',
      minimum: 1,
      description: 'Foreign key to the parent Subcategory',
    },
    comentario: {
      type: 'string',
      description: 'Optional comment — always present in response (empty string when not provided)',
    },
  },
}

/**
 * JSON Schema for a list of Transaction objects.
 * Used to validate GET /v1/lancamentos responses.
 */
export const transactionListSchema = {
  type: 'array',
  items: transactionSchema,
}
