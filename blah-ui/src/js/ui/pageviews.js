var h = require('snabbdom/h')
var nav = require('./nav')
var error = require('./error')
var container = require('./container')
var widgets = require('./widgets')
var masonry = require('./masonry')

function render(model, update, ws, storage) {
  return container(model, [
    nav(model, update, ws, storage),
    h('h1.has-text-centered', 'Pageviews'),
    (model.error) ? error.unknown() : masonry({
      class: {'widgets': true},
      itemSelector: '.widget'
    }, [
      widgets.browserStats(update, ws),
      widgets.pageviews(ws),
      widgets.countAll(ws),
      widgets.countOne(ws, 'item-1'),
      widgets.platformStats(ws),
      widgets.pageviewDiff(ws),
      widgets.mobileOsStats(ws)
    ])
  ])
}

module.exports = render
