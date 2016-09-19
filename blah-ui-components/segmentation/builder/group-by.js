var h = require('snabbdom/h')
var ctrl = require('./ctrl')

function render(options) {
  return function(model, update) {
    return h('div.group-row', [h('div.control.is-horizontal', [
      h('div.control-label', [h('label.label', 'Group by')]),
      h('div.control', [h('select.select', {
        on: {change: function(e) {
          var selected = []
          for(var i in e.target.options) {
            if(e.target.options[i].selected) {
              selected.push(e.target.options[i].value)
            }
          }
          update(ctrl.updateGroups, selected)
        }}
      }, options.groups.map(function(x) {
        return h('option', {
          props: {
            value: x,
            selected: -1 !== model.groupBy.indexOf(x)
          }
        }, x)
      }))])
    ])])
  }
}

module.exports = render
