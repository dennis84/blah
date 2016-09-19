var h = require('snabbdom/h')
var nav = require('./nav')
var error = require('./error')
var container = require('./container')
var widgets = require('./widgets')
var masonry = require('./masonry')

function render(model, update, ws, storage) {
  return container(model, [
    nav(model, update, ws, storage),
    h('h1.has-text-centered', 'User Stats'),
    (model.error) ? error.unknown() : masonry({
      class: {'widgets': true},
      itemSelector: '.widget'
    }, [
      widgets.visitorsToday(ws),
      widgets.visitorsByCountry(ws),
      widgets.uniqueVisitors(ws)
    ])
  ])
}

module.exports = render
