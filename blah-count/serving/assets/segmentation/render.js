var h = require('snabbdom/h').default
var dateFilterAbs = require('./date-filter-abs')
var dateFilterQuick = require('./date-filter-quick')
var filterBy = require('./filter-by')
var groupBy = require('./group-by')
var ctrl = require('./ctrl')
var util = require('../chart/util')
var lines = require('../chart/lines')

function chart(model) {
  if(0 === model.segments.length) return h('div.is-empty')
  var diff = util.dateDiff(model.to, model.from, 'day')
  var format = 'ddd h:mm a'
  if(diff > 2) format = 'ddd DD'
  if(diff > 60) format = 'MMM YYYY'
  var step = model.groupBy.find(function(x) {
    return 0 === x.indexOf('date.')
  }).split('.')[1]

  var data = model.segments.map(function(segment) {
    return util.timeframe(segment.data, model.from, model.to, {
      format: format,
      step: step
    })
  })

  var updateChart = function(vnode) {
    lines(vnode.elm, data)
  }

  return h('div.chart', {
    hook: {
      insert: updateChart,
      update: updateChart
    }
  })
}

function render(model, update) {
  var dateFilters = []
  if('absolute' === model.dateFilterMode) {
    dateFilters.push(dateFilterAbs(model, update))
    dateFilters.push(groupBy(model, update))
  } else {
    dateFilters.push(dateFilterQuick(model, update))
  }

  return h('div.segmentation', [
    h('h1.title.is-1.has-text-centered', 'Segmentation'),
    h('div.card.is-fullwidth', [
      h('div.card-content', [
        h('div.content', dateFilters)
      ]),
      h('footer.card-footer', [
        h('a.card-footer-item', {
          on: {click: function() {
            update(ctrl.setDateFilterMode, 'quick')
          }}
        }, 'Quick'),
        h('a.card-footer-item', {
          on: {click: function() {
            update(ctrl.setDateFilterMode, 'absolute')
          }}
        }, 'Absolute'),
      ])
    ]),
    h('div.segments', model.segments.map(function(seg) {
      return h('div.card.is-fullwidth.segment', [
        seg.filterBy.length ? filterBy(seg, update) : h('span'),
        h('footer.card-footer', [
          h('a.card-footer-item', {
            on: {click: function() {
              update(ctrl.addSegmentFilter, seg)
            }}
          }, 'Add filter'),
          h('a.card-footer-item', {
            on: {click: function() {
              update(ctrl.removeSegment)
            }}
          }, 'Remove segment')
        ])
      ])
    })),
    h('div.card.is-fullwidth', [
      h('footer.card-footer', [
        h('a.card-footer-item', {
          on: {click: function() {
            update(ctrl.addSegment)
          }}
        }, 'Add segment')
      ])
    ]),
    h('div.box', {
      class: model.class
    }, chart(model))
  ])
}

module.exports = render
