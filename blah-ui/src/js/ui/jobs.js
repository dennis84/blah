var nav = require('./nav')
var component = require('./component')
var container = require('./container')

function render(model, update) {
  return container(model, [
    nav(model, update),
    component(window.Jobs, {}, {})
  ])
}

module.exports = render
