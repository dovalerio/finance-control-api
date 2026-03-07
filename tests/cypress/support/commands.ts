const API_KEY = 'aXRhw7o='

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
  return cy.request<T>({
    failOnStatusCode: false,
    ...options,
    headers: {
      'Content-Type': 'application/json',
      'api-key': API_KEY,
      ...options.headers,
    },
  })
})
