var h = require('snabbdom/h')
var nav = require('./nav')
var error = require('./error')
var component = require('./component')
var container = require('./container')

function render(model, update, ws, storage) {
  return container(model, [
    nav(model, update, ws, storage),
    h('h1.has-text-centered', 'World Map'),
    (model.error) ? error.unknown() : component(window.WorldMap, {
      class: {'world-map': true}
    }, ws)
  ])
}

module.exports = render
