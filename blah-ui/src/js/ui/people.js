var h = require('snabbdom/h').default
var nav = require('./nav')
var error = require('./error')
var container = require('./container')
var component = require('./component')

function render(model, update, events, storage) {
  return container(model, [
    nav(model, update, storage),
    (model.error) ? error.unknown() : component(window.People, {}, {})
  ])
}

module.exports = render
