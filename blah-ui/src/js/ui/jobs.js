var nav = require('./nav')
var error = require('./error')
var component = require('./component')
var container = require('./container')

function render(model, update, events, storage) {
  return container(model, [
    nav(model, update, storage),
    (model.error) ? error.unknown() : component(window.Jobs, {}, {})
  ])
}

module.exports = render
