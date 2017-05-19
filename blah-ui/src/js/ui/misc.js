var h = require('snabbdom/h').default
var nav = require('./nav')
var container = require('./container')
var widgets = require('./widgets')
var masonry = require('./masonry')

function render(model, update, events) {
  return container(model, [
    nav(model, update),
    h('h1.has-text-centered', 'Misc'),
    masonry({
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
