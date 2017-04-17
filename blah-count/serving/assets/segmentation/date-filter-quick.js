var h = require('snabbdom/h').default
var ctrl = require('./ctrl')
var addHours = require('date-fns/add_hours')
var subHours = require('date-fns/sub_hours')
var subDays = require('date-fns/sub_days')
var subMonths = require('date-fns/sub_months')
var startOfHour = require('date-fns/start_of_hour')
var startOfDay = require('date-fns/start_of_day')
var startOfMonth = require('date-fns/start_of_month')

function createInput(update, label, options) {
  return h('p.control', [
    h('label.radio', [
      h('input', {
        props :{type: 'radio', name: 'timepicker-quick'},
        on: {change: function() {
          var now = startOfHour(addHours(Date.now(), 1)).toISOString()
          update(ctrl.updateGroupBy, options.groupBy)
          update(ctrl.updateFrom, options.from.toISOString())
          update(ctrl.updateTo, now)
        }}
      }),
      h('span', label)
    ])
  ])
}

function render(model, update) {
  return h('div.columns', [
    h('div.column', [
      createInput(update, 'Last 4 hours', {
        from: startOfHour(subHours(Date.now(), 4)),
        groupBy: ['date.hour']
      }),
      createInput(update, 'Last 12 hours', {
        from: startOfHour(subHours(Date.now(), 12)),
        groupBy: ['date.hour']
      }),
      createInput(update, 'Last 24 hours', {
        from: startOfHour(subHours(Date.now(), 24)),
        groupBy: ['date.hour']
      }),
      createInput(update, 'Last 7 days', {
        from: startOfDay(subDays(Date.now(), 7)),
        groupBy: ['date.day']
      })
    ]),
    h('div.column', [
      createInput(update, 'Last 30 days', {
        from: startOfDay(subDays(Date.now(), 30)),
        groupBy: ['date.day']
      }),
      createInput(update, 'Last 60 days', {
        from: startOfDay(subDays(Date.now(), 60)),
        groupBy: ['date.day']
      }),
      createInput(update, 'Last 6 months', {
        from: startOfMonth(subMonths(Date.now(), 6)),
        groupBy: ['date.month']
      }),
      createInput(update, 'Last 1 year', {
        from: startOfMonth(subMonths(Date.now(), 12)),
        groupBy: ['date.month']
      })
    ])
  ])
}

module.exports = render
