var h = require('snabbdom/h')
var moment = require('moment')
var Pikaday = require('pikaday')
var ctrl = require('./ctrl')

function createPickaday(vnode) {
  vnode.elm.picker = new Pikaday({
    field: vnode.elm,
    format: 'YYYY-MM-DD'
  })
}

function formatDate(d) {
  return d ? moment(d).format('MMM Do, YYYY') : ''
}

function render(model, update) {
  return h('div.control.is-horizontal', [
    h('div.control-label', [h('label.label', 'From - To')]),
    h('div.control.is-grouped', [
      h('p.control.is-expanded', [
        h('input.input', {
          hook: {insert: createPickaday},
          props: {value: formatDate(model.from)},
          on: {change: function(e) {
            if(e.currentTarget.picker) {
              update(ctrl.updateFrom, moment(e.currentTarget.value).toISOString())
            }
          }}
        })
      ]),
      h('p.control.is-expanded', [
        h('input.input', {
          hook: {insert: createPickaday},
          props: {value: formatDate(model.to)},
          on: {change: function(e) {
            if(e.currentTarget.picker) {
              update(ctrl.updateTo, moment(e.currentTarget.value).toISOString())
            }
          }}
        })
      ])
    ])
  ])
}

module.exports = render
