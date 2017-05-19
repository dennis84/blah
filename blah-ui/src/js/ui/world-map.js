var nav = require('./nav')
var component = require('./component')
var container = require('./container')

function render(model, update, events) {
  return container(model, [
    nav(model, update),
    component(window.WorldMap, {}, events)
  ])
}

module.exports = render
