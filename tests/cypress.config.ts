import { defineConfig } from 'cypress'

export default defineConfig({
  reporter: 'cypress-multi-reporters',
  reporterOptions: {
    reporterEnabled: 'spec, mochawesome',
    mochawesomeReporterOptions: {
      reportDir: 'reports/json',
      overwrite: false,
      html: false,
      json: true,
      quiet: true,
    },
  },
  e2e: {
    baseUrl: 'http://localhost:8080/v1',
    specPattern: 'cypress/e2e/**/*.cy.ts',
    supportFile: 'cypress/support/e2e.ts',
    fixturesFolder: 'cypress/fixtures',
    video: false,
    screenshotOnRunFailure: false,
    requestTimeout: 10000,
    responseTimeout: 30000,
  },
})
