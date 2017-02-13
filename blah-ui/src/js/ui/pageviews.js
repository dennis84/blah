var h = require('snabbdom/h').default
var nav = require('./nav')
var error = require('./error')
var container = require('./container')
var widgets = require('./widgets')
var masonry = require('./masonry')

function render(model, update, events, storage) {
  return container(model, [
    nav(model, update, storage),
    h('h1.has-text-centered', 'Pageviews'),
    (model.error) ? error.unknown() : masonry({
      class: {'widgets': true},
      itemSelector: '.widget'
    }, [
      widgets.browserStats(update, events),
      widgets.pageviews(events),
      widgets.countAll(events),
      widgets.countOne(events, 'item-1'),
      widgets.platformStats(events),
      widgets.pageviewDiff(events),
      widgets.mobileOsStats(events)
    ])
  ])
}

module.exports = render
