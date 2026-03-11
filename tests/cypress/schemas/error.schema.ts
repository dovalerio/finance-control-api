/**
 * JSON Schema for the standard error envelope.
 *
 * Every 4xx/5xx response from this API must match this shape
 * (desafio.pdf requirement):
 *
 *   { "codigo": "erro_validacao", "mensagem": "O campo 'nome' é obrigatório" }
 *
 * Known codigo values:
 *   - erro_validacao   → 400 (missing/invalid fields)
 *   - nao_encontrado   → 404
 *   - conflito         → 409 (uniqueness or linked-entity constraint)
 *   - valor_invalido   → 422 (valor == 0)
 *   - nao_autorizado   → 401
 *   - periodo_invalido → 400 (data_fim < data_inicio)
 *   - corpo_invalido   → 400 (malformed JSON body)
 */
export const errorSchema = {
  type: 'object',
  required: ['codigo', 'mensagem'],
  additionalProperties: false,
  properties: {
    codigo: {
      type: 'string',
      minLength: 1,
      description: 'Machine-readable error code — unique per error type',
    },
    mensagem: {
      type: 'string',
      minLength: 1,
      description: 'Human-readable description in Portuguese',
    },
  },
}
