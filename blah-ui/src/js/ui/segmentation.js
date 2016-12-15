var h = require('snabbdom/h')
var nav = require('./nav')
var error = require('./error')
var container = require('./container')
var component = require('./component')
var config = require('./../config')
var moment = require('moment')

function render(model, update, ws, storage) {
  return container(model, [
    nav(model, update, ws, storage),
    h('h1.has-text-centered', 'Segmentation'),
    (model.error) ? error.unknown() : component(window.Segmentation, {}, ws, {
      baseUrl: config.SERVING_URL,
      collection: 'view',
      class: {'size-3of3': true, 'segmentation': true},
      groups: ['date.year', 'date.month', 'date.day', 'date.hour'],
      groupBy: ['date.hour'],
      from: moment().subtract(1, 'day').toISOString(),
      to: moment().add(2, 'hour').toISOString(),
      segments: [{
        filterBy: []
      }]
    })
  ])
}

module.exports = render
