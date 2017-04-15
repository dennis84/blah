var h = require('snabbdom/h').default
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
  return h('div.field.is-horizontal', [
    h('div.field-label.is-normal', [
      h('label.label', 'From - To')
    ]),
    h('div.field-body', [
      h('div.field.is-grouped', [
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
        ])
      ]),
      h('div.field.is-grouped', [
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
