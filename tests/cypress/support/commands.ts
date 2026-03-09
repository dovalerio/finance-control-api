const API_KEY = 'aXRhw7o='

export interface RequestLogEntry {
  method: string
  url: string
  payload: unknown
  status: number
  responseBody: unknown
  durationMs: number
}

/** Shared log cleared before each test and flushed to mochawesome in afterEach (see e2e.ts). */
export const httpLogs: RequestLogEntry[] = []

declare global {
  namespace Cypress {
    interface Chainable {
      apiRequest<T = unknown>(
        options: Partial<Cypress.RequestOptions>
      ): Chainable<Cypress.Response<T>>
    }
  }
}

Cypress.Commands.add('apiRequest', <T>(options: Partial<Cypress.RequestOptions>) => {
  const startTime = Date.now()

  return cy.request<T>({
    failOnStatusCode: false,
    ...options,
    headers: {
      'Content-Type': 'application/json',
      'api-key': API_KEY,
      ...options.headers,
    },
  }).then((response) => {
    const entry: RequestLogEntry = {
      method: (options.method ?? 'GET').toUpperCase(),
      url: String(options.url),
      payload: options.body ?? null,
      status: response.status,
      responseBody: response.body,
      durationMs: Date.now() - startTime,
    }

    httpLogs.push(entry)

    // SAFE logging that does not enqueue a Cypress command
    Cypress.log({
      name: entry.method,
      message: `${entry.url} → ${entry.status} (${entry.durationMs}ms)`,
      consoleProps: () => ({
        method: entry.method,
        url: entry.url,
        payload: entry.payload,
        status: entry.status,
        responseBody: entry.responseBody,
        durationMs: entry.durationMs,
      }),
    })

    return response
  })
})