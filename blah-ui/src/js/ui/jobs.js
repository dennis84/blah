var h = require('snabbdom/h').default
var nav = require('./nav')
var error = require('./error')
var component = require('./component')
var container = require('./container')

function render(model, update, events, storage) {
  return container(model, [
    nav(model, update, storage),
    h('h1.has-text-centered', 'Jobs'),
    (model.error) ? error.unknown() : component(window.Jobs, {}, {})
  ])
}

module.exports = render
