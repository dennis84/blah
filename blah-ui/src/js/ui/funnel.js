var nav = require('./nav')
var component = require('./component')
var container = require('./container')

function render(model, update, events) {
  return container(model, [
    nav(model, update),
    component(window.Funnel, {}, events, {
      name: 'signup',
      steps: ['landingpage', 'signup', 'dashboard'],
      title: 'Sign Up Funnel'
    })
  ])
}

module.exports = render
