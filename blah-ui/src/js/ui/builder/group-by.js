import {h} from 'virtual-dom'
import {updateGroups} from './ctrl'

function render(model, update) {
  if(undefined === model.groups) return
  return h('div.control', [
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
    }, model.groups.map((group) => h('option', {
      value: group.value,
      selected: group.selected
    }, group.value)))
  ])
}

export default render
