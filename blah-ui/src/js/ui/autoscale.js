var h = require('snabbdom/h')
var nav = require('./nav')
var error = require('./error')
var container = require('./container')
var component = require('./component')

function render(model, update, ws, storage) {
  return container(model, [
    nav(model, update, ws, storage),
    h('h1.has-text-centered', 'Autoscale'),
    (model.error) ? error.unknown() : component(window.Autoscale, {}, {})
  ])
}

module.exports = render
