import addContext from 'mochawesome/addContext'
import { httpLogs } from './commands'

beforeEach(() => {
  httpLogs.length = 0
})

afterEach(function () {
  if (httpLogs.length === 0) return

  addContext(this, {
    title: 'HTTP Requests / Responses',
    value: httpLogs.map((entry) => ({
      request: {
        method: entry.method,
        url: entry.url,
        payload: entry.payload,
      },
      response: {
        status: entry.status,
        body: entry.responseBody,
      },
      durationMs: entry.durationMs,
    })),
  })
})
