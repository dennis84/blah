var h = require('snabbdom/h').default
var ctrl = require('./ctrl')

function render(model, update) {
  return h('div.field.is-horizontal', [
    h('div.field-label.is-medium', [h('label.label', 'Group by')]),
    h('div.field-body', [
      h('div.field', h('div.control', h('div.select.is-fullwidth.is-medium', [
        h('select', {
          on: {change: function(e) {
            var selected = []
            for(var i in e.target.options) {
              if(e.target.options[i].selected) {
                selected.push(e.target.options[i].value)
              }
            }
            update(ctrl.updateGroupBy, selected)
          }}
        }, model.groups.map(function(group) {
          return h('option', {
            props: {
              value: group,
              selected: -1 !== model.groupBy.indexOf(group)
            }
          }, group)
        }))
      ])))
    ])
  ])
}

module.exports = render
