var h = require('snabbdom/h').default
var nav = require('./nav')
var container = require('./container')
var widgets = require('./widgets')
var masonry = require('./masonry')

function render(model, update, events) {
  return container(model, [
    nav(model, update),
    h('h1.has-text-centered', 'User Stats'),
    masonry({
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
