var nav = require('./nav')
var container = require('./container')
var component = require('./component')

function render(model, update) {
  return container(model, [
    nav(model, update),
    component(window.People, {}, {})
  ])
}

module.exports = render
