var h = require('snabbdom/h')
var nav = require('./nav')
var error = require('./error')
var container = require('./container')
var component = require('./component')
var config = require('./../config')

function render(model, update, ws, storage) {
  return container(model, [
    nav(model, update, ws, storage),
    h('h1.has-text-centered', 'People'),
    (model.error) ? error.unknown() : component(window.People, {
      class: {people: true}
    }, {
      baseUrl: config.SERVING_URL
    })
  ])
}

module.exports = render
