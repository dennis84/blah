var h = require('snabbdom/h')
var moment = require('moment')
var form = require('./builder/form')
var dateRange = require('./builder/date-range')
var filterBy = require('./builder/filter-by')
var groupBy = require('./builder/group-by')

function chart(model) {
  var from = model.filterBy.find(function(x) {
    return 'date.from' === x.prop
  })

  var to = model.filterBy.find(function(x) {
    return 'date.to' === x.prop
  })

  from = from ? moment(from.value) : moment().subtract(1, 'day')
  to = to ? moment(to.value).endOf('day') : moment().add(1, 'hour')
  var diff = to.diff(from, 'days')
  var format = 'ddd h:mm a'
  if(diff > 2) format = 'ddd M'
  if(diff > 7) format = 'MMM DD'
  if(diff > 60) format = 'MMM YYYY'

  var data = Chart.timeframe(model.groups, from, to, {format: format})
  var updateChart = function(vnode) {
    Chart.line(vnode.elm, data)
  }

  return h('div.chart', {
    hook: {
      insert: updateChart,
      update: updateChart
    }
  })
}

function render(model, update, options) {
  return h('div', [
    form(model, update, dateRange, filterBy, groupBy(options)),
    h('div.widget.widget-line', {
      props: {className: options.className},
    }, [
      h('h3', options.title),
      chart(model)
    ])
  ])
}

module.exports = render
