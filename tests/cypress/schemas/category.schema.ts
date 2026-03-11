/**
 * JSON Schema for the Category resource.
 *
 * Actual API response (confirmed):
 *   { "id_categoria": 120, "nome": "Transporte" }
 */
export const categorySchema = {
  type: 'object',
  required: ['id_categoria', 'nome'],
  additionalProperties: false,
  properties: {
    id_categoria: {
      type: 'integer',
      minimum: 1,
      description: 'Auto-generated unique positive identifier',
    },
    nome: {
      type: 'string',
      minLength: 1,
      description: 'Category name — unique in the system',
    },
  },
}

/**
 * JSON Schema for a list of Category objects.
 * Used to validate GET /v1/categorias responses.
 */
export const categoryListSchema = {
  type: 'array',
  items: categorySchema,
}
