var h = require('snabbdom/h').default
var nav = require('./nav')
var container = require('./container')
var widgets = require('./widgets')
var masonry = require('./masonry')

function render(model, update, events) {
  return container(model, [
    nav(model, update),
    h('h1.has-text-centered', 'Pageviews'),
    masonry({
      class: {'widgets': true},
      itemSelector: '.widget'
    }, [
      widgets.browserStats(update, events),
      widgets.pageviews(events),
      widgets.countAll(events),
      widgets.countOne(events, 'item-1'),
      widgets.platformStats(events),
      widgets.mobileOsStats(events)
    ])
  ])
}

module.exports = render
