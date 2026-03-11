/**
 * JSON Schema for the Subcategory resource.
 *
 * Actual API response (confirmed):
 *   { "id_subcategoria": 90, "nome": "Farmácia", "id_categoria": 3 }
 *
 * Domain rules (desafio.pdf):
 *   - nome must be unique within the same id_categoria
 *   - id_categoria must reference an existing Category
 *   - subcategory is cascade-deleted when its parent Category is removed
 */
export const subcategorySchema = {
  type: 'object',
  required: ['id_subcategoria', 'nome', 'id_categoria'],
  additionalProperties: false,
  properties: {
    id_subcategoria: {
      type: 'integer',
      minimum: 1,
      description: 'Auto-generated unique positive identifier',
    },
    nome: {
      type: 'string',
      minLength: 1,
      description: 'Subcategory name — unique within the same category',
    },
    id_categoria: {
      type: 'integer',
      minimum: 1,
      description: 'Foreign key to the parent Category',
    },
  },
}

/**
 * JSON Schema for a list of Subcategory objects.
 * Used to validate GET /v1/subcategorias responses.
 */
export const subcategoryListSchema = {
  type: 'array',
  items: subcategorySchema,
}
