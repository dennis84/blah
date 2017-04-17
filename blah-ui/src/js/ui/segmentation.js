var h = require('snabbdom/h').default
var nav = require('./nav')
var error = require('./error')
var container = require('./container')
var component = require('./component')
var subDays = require('date-fns/sub_days')
var addHours = require('date-fns/add_hours')

function render(model, update, events, storage) {
  return container(model, [
    nav(model, update, storage),
    h('h1.has-text-centered', 'Segmentation'),
    (model.error) ? error.unknown() : component(window.Segmentation, {}, events, {
      collection: 'view',
      class: {'size-3of3': true, 'segmentation': true},
      groups: ['date.year', 'date.month', 'date.day', 'date.hour'],
      groupBy: ['date.hour'],
      from: subDays(Date.now(), 1).toISOString(),
      to: addHours(Date.now(), 1).toISOString(),
      segments: []
    })
  ])
}

module.exports = render
