/**
 * JSON Schemas for the Balance (Balanço) resource.
 *
 * The API returns two distinct shapes depending on whether id_categoria
 * is provided as a query parameter:
 *
 * Without id_categoria (confirmed response):
 *   { "receita": "8719.45", "despesa": "600.00", "saldo": "8119.45" }
 *
 * With id_categoria (confirmed response):
 *   {
 *     "categoria": { "id_categoria": 120, "nome": "SchemaTest-Cat" },
 *     "receita": "123.45",
 *     "despesa": "0.00",
 *     "saldo": "123.45"
 *   }
 *
 * Note: receita/despesa/saldo are STRINGS (e.g. "2320.00"), not numbers.
 * This matches the desafio.pdf example and the confirmed API behaviour.
 * saldo can be negative (e.g. "-200.00") when expenses exceed income.
 */

// Reusable sub-schema for monetary string values: "2320.00" / "-200.00" / "0.00"
const monetaryString = {
  type: 'string',
  // Optional leading minus; one or more digits; dot; exactly two decimal places
  pattern: '^-?\\d+\\.\\d{2}$',
  description: 'Decimal string with 2 decimal places, e.g. "1320.00" or "-200.00"',
}

// Inline category shape used only inside balance responses
const inlineCategory = {
  type: 'object',
  required: ['id_categoria', 'nome'],
  additionalProperties: false,
  properties: {
    id_categoria: { type: 'integer', minimum: 1 },
    nome: { type: 'string', minLength: 1 },
  },
}

/**
 * Schema for GET /v1/balanco WITHOUT id_categoria filter.
 * The "categoria" field must NOT be present.
 */
export const balanceSchema = {
  type: 'object',
  required: ['receita', 'despesa', 'saldo'],
  additionalProperties: false,
  properties: {
    receita: monetaryString,
    despesa: monetaryString,
    saldo:   monetaryString,
  },
}

/**
 * Schema for GET /v1/balanco WITH id_categoria filter.
 * The "categoria" object is required and must match the Category shape.
 */
export const balanceWithCategorySchema = {
  type: 'object',
  required: ['receita', 'despesa', 'saldo', 'categoria'],
  additionalProperties: false,
  properties: {
    receita:   monetaryString,
    despesa:   monetaryString,
    saldo:     monetaryString,
    categoria: inlineCategory,
  },
}
