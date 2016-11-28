var h = require('snabbdom/h')
var moment = require('moment')
var Pikaday = require('pikaday')
var ctrl = require('./ctrl')

function mkInput(filter, index, update) {
  return h('input.input', {
    hook: {
      insert: function(vnode) {
        vnode.elm.picker = new Pikaday({
          field: vnode.elm,
          format: 'YYYY-MM-DD'
        })
      }
    },
    props: {
      value: filter.value ? moment(filter.value).format('MMM Do, YYYY') : ''
    },
    on: {change: function(e) {
      if(e.currentTarget.picker) {
        update(ctrl.updateFilter, index, {
          value: moment(e.currentTarget.value).toISOString()
        })
      }
    }}
  })
}

function render(model, update) {
  var filters = model.filterBy || []
  var from = filters.find(function(x) {
    return 'date.from' === x.prop
  })
  var fromIndex = filters.indexOf(from)
  var to = filters.find(function(x) {
    return 'date.to' === x.prop
  })
  var toIndex = filters.indexOf(to)

  return h('div.filter-row', [h('div.control.is-grouped.is-horizontal', [
    h('div.control-label', [h('label.label', 'From - To')]),
    h('div.control', [
      mkInput(from, fromIndex, update),
      mkInput(to, toIndex, update)
    ])
  ])])
}

module.exports = render
