var h = require('snabbdom/h')
var nav = require('./nav')
var error = require('./error')
var component = require('./component')
var container = require('./container')
var config = require('./../config')

function render(model, update, ws, storage) {
  return container(model, [
    nav(model, update, ws, storage),
    h('h1.has-text-centered', 'Sign Up Funnel'),
    (model.error) ? error.unknown() : component(Funnel, {}, ws, {
      baseUrl: config.SERVING_URL,
      class: {'size-3of3': true, 'funnel': true},
      name: 'signup',
      steps: ['landingpage', 'signup', 'dashboard']
    })
  ])
}

module.exports = render
