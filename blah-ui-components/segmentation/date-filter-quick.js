var h = require('snabbdom/h')
var moment = require('moment')
var ctrl = require('./ctrl')

function createInput(update, label, options) {
  return h('p.control', [
    h('label.radio', [
      h('input', {
        props :{type: 'radio', name: 'timepicker-quick'},
        on: {change: function() {
          var now = moment().add(1, 'hour').startOf('hour').toISOString()
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
        from: moment().startOf('hour').subtract(4, 'hours'),
        groupBy: ['date.hour']
      }),
      createInput(update, 'Last 12 hours', {
        from: moment().startOf('hour').subtract(12, 'hours'),
        groupBy: ['date.hour']
      }),
      createInput(update, 'Last 24 hours', {
        from: moment().startOf('hour').subtract(24, 'hours'),
        groupBy: ['date.hour']
      }),
      createInput(update, 'Last 7 days', {
        from: moment().startOf('day').subtract(7, 'days'),
        groupBy: ['date.day']
      })
    ]),
    h('div.column', [
      createInput(update, 'Last 30 days', {
        from: moment().startOf('day').subtract(30, 'days'),
        groupBy: ['date.day']
      }),
      createInput(update, 'Last 60 days', {
        from: moment().startOf('day').subtract(60, 'days'),
        groupBy: ['date.day']
      }),
      createInput(update, 'Last 6 months', {
        from: moment().startOf('month').subtract(6, 'months'),
        groupBy: ['date.month']
      }),
      createInput(update, 'Last 1 year', {
        from: moment().startOf('month').subtract(1, 'year'),
        groupBy: ['date.month']
      })
    ])
  ])
}

module.exports = render
