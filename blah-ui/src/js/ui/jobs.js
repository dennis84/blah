var h = require('snabbdom/h')
var nav = require('./nav')
var error = require('./error')
var component = require('./component')
var container = require('./container')
var config = require('./../config')

function render(model, update, ws, storage) {
  return container(model, [
    nav(model, update, ws, storage),
    h('h1.has-text-centered', 'Jobs'),
    (model.error) ? error.unknown() : component(Jobs, {}, {
      baseUrl: config.SERVING_URL
    })
  ])
}

module.exports = render
