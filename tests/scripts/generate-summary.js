'use strict'

const fs = require('fs')
const path = require('path')

const REPORT_IN  = path.join(__dirname, '..', 'reports', 'mochawesome.json')
const REPORT_OUT = path.join(__dirname, '..', 'reports', 'summary-report.html')

if (!fs.existsSync(REPORT_IN)) {
  console.error(`[summary] Merged report not found: ${REPORT_IN}`)
  console.error('[summary] Run "npm run report:generate" first.')
  process.exit(1)
}

const report = JSON.parse(fs.readFileSync(REPORT_IN, 'utf8'))
const { stats, results } = report

const totalDurationSec = ((stats.duration || 0) / 1000).toFixed(2)
const passRate = stats.tests > 0 ? Math.round((stats.passes / stats.tests) * 100) : 0
const generatedAt = new Date().toLocaleString()

const specRows = (results || []).map((result) => {
  const fileName = path.basename(result.fullFile || result.file || 'unknown')
  const isPass = (result.totalFailures || 0) === 0
  return {
    fileName,
    total:    result.totalTests    || 0,
    passes:   result.totalPasses   || 0,
    failures: result.totalFailures || 0,
    duration: ((result.duration || 0) / 1000).toFixed(2),
    isPass,
  }
})

const specTableRows = specRows.map(({ fileName, total, passes, failures, duration, isPass }) => `
    <tr>
      <td>
        <span class="badge ${isPass ? 'badge-pass' : 'badge-fail'}">${isPass ? '✓' : '✗'}</span>
        ${fileName}
      </td>
      <td class="center">${total}</td>
      <td class="center pass-text">${passes}</td>
      <td class="center ${failures > 0 ? 'fail-text' : ''}">${failures}</td>
      <td class="center muted">${duration}s</td>
    </tr>`).join('')

const html = `<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Test Summary — Finance Control API</title>
  <style>
    *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
    body {
      font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
      background: #f0f2f5;
      color: #1a1a2e;
      padding: 40px 16px;
    }
    .container { max-width: 860px; margin: 0 auto; }
    h1 { font-size: 1.7rem; font-weight: 700; margin-bottom: 4px; }
    .meta { font-size: 0.82rem; color: #6b7280; margin-bottom: 32px; }
    .cards {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 14px;
      margin-bottom: 18px;
    }
    @media (max-width: 600px) { .cards { grid-template-columns: repeat(2, 1fr); } }
    .card {
      background: #fff;
      border-radius: 12px;
      padding: 20px 16px;
      text-align: center;
      box-shadow: 0 1px 4px rgba(0,0,0,.07);
    }
    .card .num { font-size: 2.4rem; font-weight: 800; line-height: 1; }
    .card .lbl { font-size: 0.75rem; text-transform: uppercase; letter-spacing: .06em; color: #6b7280; margin-top: 6px; }
    .card.total  .num { color: #3b82f6; }
    .card.passed .num { color: #22c55e; }
    .card.failed .num { color: #ef4444; }
    .card.time   .num { color: #f59e0b; }
    .progress-wrap {
      background: #e5e7eb;
      border-radius: 9999px;
      height: 8px;
      margin-bottom: 32px;
      overflow: hidden;
    }
    .progress-fill {
      height: 100%;
      border-radius: 9999px;
      background: linear-gradient(90deg, #22c55e, #16a34a);
    }
    .section-title { font-size: 0.95rem; font-weight: 600; color: #374151; margin-bottom: 12px; }
    table {
      width: 100%;
      border-collapse: collapse;
      background: #fff;
      border-radius: 12px;
      overflow: hidden;
      box-shadow: 0 1px 4px rgba(0,0,0,.07);
    }
    thead { background: #f9fafb; }
    th {
      padding: 11px 16px;
      text-align: left;
      font-size: 0.75rem;
      text-transform: uppercase;
      letter-spacing: .05em;
      color: #6b7280;
    }
    td { padding: 11px 16px; font-size: 0.88rem; border-top: 1px solid #f3f4f6; }
    .center { text-align: center; }
    .pass-text { color: #16a34a; font-weight: 600; }
    .fail-text { color: #dc2626; font-weight: 600; }
    .muted { color: #9ca3af; }
    .badge {
      display: inline-block;
      width: 18px; height: 18px;
      border-radius: 50%;
      text-align: center;
      line-height: 18px;
      font-size: 0.65rem;
      font-weight: 700;
      margin-right: 6px;
      vertical-align: middle;
    }
    .badge-pass { background: #dcfce7; color: #16a34a; }
    .badge-fail { background: #fee2e2; color: #dc2626; }
    .footer { margin-top: 24px; text-align: center; font-size: 0.75rem; color: #9ca3af; }
    .footer a { color: #6b7280; }
  </style>
</head>
<body>
  <div class="container">
    <h1>Test Summary Report</h1>
    <p class="meta">Finance Control API &nbsp;·&nbsp; Generated ${generatedAt}</p>

    <div class="cards">
      <div class="card total">
        <div class="num">${stats.tests}</div>
        <div class="lbl">Total Tests</div>
      </div>
      <div class="card passed">
        <div class="num">${stats.passes}</div>
        <div class="lbl">Passed</div>
      </div>
      <div class="card failed">
        <div class="num">${stats.failures}</div>
        <div class="lbl">Failed</div>
      </div>
      <div class="card time">
        <div class="num">${totalDurationSec}s</div>
        <div class="lbl">Duration</div>
      </div>
    </div>

    <div class="progress-wrap">
      <div class="progress-fill" style="width:${passRate}%"></div>
    </div>

    <p class="section-title">Spec Results &mdash; ${passRate}% pass rate</p>
    <table>
      <thead>
        <tr>
          <th>Spec File</th>
          <th class="center">Total</th>
          <th class="center">Passed</th>
          <th class="center">Failed</th>
          <th class="center">Duration</th>
        </tr>
      </thead>
      <tbody>${specTableRows}
      </tbody>
    </table>

    <p class="footer">
      See full details: <a href="detailed-report.html">detailed-report.html</a>
    </p>
  </div>
</body>
</html>`

fs.mkdirSync(path.dirname(REPORT_OUT), { recursive: true })
fs.writeFileSync(REPORT_OUT, html, 'utf8')
console.log(`[summary] Report saved: ${REPORT_OUT}`)
