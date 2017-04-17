var h = require('snabbdom/h').default
var Pikaday = require('pikaday')
var ctrl = require('./ctrl')
var format = require('date-fns/format')

function createPickaday(vnode) {
  vnode.elm.picker = new Pikaday({
    field: vnode.elm,
    format: 'YYYY-MM-DD'
  })
}

function formatDate(d) {
  return d ? format(d, 'MMM Do, YYYY') : ''
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
                var date = new Date(e.currentTarget.value)
                update(ctrl.updateFrom, date.toISOString())
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
              var date = new Date(e.currentTarget.value)
              update(ctrl.updateTo, date.toISOString())
            }
          }}
        })
      ])
    ])
  ])
}

module.exports = render
