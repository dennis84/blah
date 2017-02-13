var h = require('snabbdom/h').default
var nav = require('./nav')
var error = require('./error')
var container = require('./container')
var widgets = require('./widgets')
var masonry = require('./masonry')

function render(model, update, events, storage) {
  return container(model, [
    nav(model, update, storage),
    h('h1.has-text-centered', 'Misc'),
    (model.error) ? error.unknown() : masonry({
      class: {'widgets': true},
      itemSelector: '.widget'
    }, [
      widgets.totalRevenue(events),
      widgets.collectionCount(events),
      widgets.recommendationWidget(update),
      widgets.similarityWidget(update),
      widgets.mostViewed(events),
      widgets.referringSites(events)
    ])
  ])
}

module.exports = render
