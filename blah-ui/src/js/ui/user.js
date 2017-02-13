var h = require('snabbdom/h').default
var nav = require('./nav')
var error = require('./error')
var container = require('./container')
var widgets = require('./widgets')
var masonry = require('./masonry')

function render(model, update, events, storage) {
  return container(model, [
    nav(model, update, storage),
    h('h1.has-text-centered', 'User Stats'),
    (model.error) ? error.unknown() : masonry({
      class: {'widgets': true},
      itemSelector: '.widget'
    }, [
      widgets.visitorsToday(events),
      widgets.visitorsByCountry(events),
      widgets.uniqueVisitors(events)
    ])
  ])
}

module.exports = render
