var h = require('virtual-dom/h')
var component = require('./component')
var pageviewWidget = require('./pageviews/widget')

function render(conn, text) {
  return h('div.dashboard', [
    h('h1', 'Dashboard ' + text),
    h('div.widgets', [
      component(pageviewWidget, conn)
    ])
  ])
}

module.exports = render
