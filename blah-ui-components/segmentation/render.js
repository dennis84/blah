var h = require('snabbdom/h')
var moment = require('moment')
var dateRange = require('./date-range')
var filterBy = require('./filter-by')
var groupBy = require('./group-by')
var ctrl = require('./ctrl')

function chart(model) {
  if(0 === model.segments.length) return h('div.is-empty')

  var from = moment(model.from)
  var to = moment(model.to)
  var diff = to.diff(from, 'days')
  var format = 'ddd h:mm a'
  if(diff > 2) format = 'ddd M'
  if(diff > 7) format = 'MMM DD'
  if(diff > 60) format = 'MMM YYYY'

  var data = model.segments.map(function(segment) {
    return window.Chart.timeframe(segment.data, from, to, {format: format})
  })

  var updateChart = function(vnode) {
    window.Chart.lines(vnode.elm, data)
  }

  return h('div.chart', {
    hook: {
      insert: updateChart,
      update: updateChart
    }
  })
}

function render(model, update) {
  return h('div', [
    dateRange(model, update),
    groupBy(model, update),
    h('div', model.segments.map(function(segment) {
      return h('div.segment', [
        filterBy(segment, update),
        h('button.button', {
          on: {click: function() {
            update(ctrl.removeSegment)
          }}
        }, 'Remove segment')
      ])
    })),
    h('button.button', {
      on: {click: function() {
        update(ctrl.addSegment)
      }}
    }, 'Add segment'),
    h('div.widget.widget-line', {
      class: model.class
    }, [
      h('h3', model.title),
      chart(model)
    ])
  ])
}

module.exports = render
