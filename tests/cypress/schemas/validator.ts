import Ajv from 'ajv'

// Single Ajv instance shared across the test suite.
// allErrors: true  → collects every violation, not just the first.
// strict: false    → allows patterns like { type: 'integer' } without
//                    requiring { type: 'integer', $schema: '...' }.
const ajv = new Ajv({ allErrors: true, strict: false })

// WeakMap cache: compiled ValidateFunctions are reused across calls so that
// ajv.compile() (which generates a Function internally) runs at most once
// per schema object reference.
const cache = new WeakMap<object, ReturnType<typeof ajv.compile>>()

function compileOnce(schema: object): ReturnType<typeof ajv.compile> {
  if (!cache.has(schema)) {
    cache.set(schema, ajv.compile(schema))
  }
  return cache.get(schema)!
}

/**
 * Validates `body` against `schema` and throws a Cypress-friendly assertion
 * error listing every violation if validation fails.
 *
 * Usage inside a `.then()` callback:
 *
 *   categoryApi.create({ nome: 'Food' }).then((res) => {
 *     assertSchema('Category', res.body, categorySchema)
 *   })
 */
export function assertSchema(label: string, body: unknown, schema: object): void {
  const validate = compileOnce(schema)
  const valid    = validate(body)

  if (!valid) {
    const lines = (validate.errors ?? [])
      .map((e) => {
        const path = e.instancePath ? `response.body${e.instancePath}` : 'response.body'
        return `  • ${path}: ${e.message}`
      })
      .join('\n')

    // expect(false).to.be.true raises a Chai AssertionError that Cypress
    // renders in the command log with the custom message.
    expect(false, `Schema "${label}" contract violation:\n${lines}`).to.be.true
  }
}

/**
 * Validates that `body` is an array and that every element satisfies
 * `itemSchema`.
 *
 *   categoryApi.list().then((res) => {
 *     assertListSchema('Category[]', res.body, categorySchema)
 *   })
 */
export function assertListSchema(
  label: string,
  body: unknown,
  itemSchema: object,
): void {
  expect(body, `${label} must be an array`).to.be.an('array')

  ;(body as unknown[]).forEach((item, i) => {
    assertSchema(`${label}[${i}]`, item, itemSchema)
  })
}
