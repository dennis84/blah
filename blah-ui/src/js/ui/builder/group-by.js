import {h} from 'virtual-dom'
import {updateGroups} from './ctrl'

function render(options) {
  return (model, update) => h('div.control', [
    h('label', 'Group by'),
    h('select', {
      multiple: true,
      onchange: (e) => {
        var selected = []
        for(var i in e.target.options) {
          if(e.target.options[i].selected) {
            selected.push(e.target.options[i].value)
          }
        }
        update(updateGroups, selected)
      }
    }, options.groups.map(x => h('option', {
      value: x,
      selected: -1 !== model.groupBy.indexOf(x)
    }, x)))
  ])
}

export default render
