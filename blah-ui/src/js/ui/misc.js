var h = require('snabbdom/h')
var nav = require('./nav')
var error = require('./error')
var container = require('./container')
var widgets = require('./widgets')
var masonry = require('./masonry')

function render(model, update, ws, storage) {
  return container(model, [
    nav(model, update, ws, storage),
    h('h1.has-text-centered', 'Misc'),
    (model.error) ? error.unknown() : masonry({
      class: {'widgets': true},
      itemSelector: '.widget'
    }, [
      widgets.totalRevenue(ws),
      widgets.collectionCount(ws),
      widgets.recommendationWidget(update),
      widgets.similarityWidget(update),
      widgets.mostViewed(ws),
      widgets.referringSites(ws)
    ])
  ])
}

module.exports = render
