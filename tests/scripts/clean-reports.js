'use strict'

const fs = require('fs')
const path = require('path')

const reportsDir = path.join(__dirname, '..', 'reports')
fs.rmSync(reportsDir, { recursive: true, force: true })
console.log('[clean-reports] reports/ cleared.')
