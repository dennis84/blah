import {h} from 'virtual-dom'
import moment from 'moment'
import Pikaday from 'pikaday'
import {mount} from '../../hook'
import {updateFilter} from './ctrl'

function mkInput(filter, index, update) {
  return h('input.input', {
    value: filter.value ? moment(filter.value).format('MMM Do, YYYY') : '',
    mount: mount(node => {
      node.picker = new Pikaday({field: node, format: 'YYYY-MM-DD'})
    }),
    onchange: e => {
      if(e.currentTarget.picker) {
        update(updateFilter, index, {
          value: moment(e.currentTarget.value).toISOString()
        })
      }
    }
  })
}

function render(model, update) {
  var filters = model.filterBy || []
  var from = filters.find(x => 'date.from' === x.prop)
  var fromIndex = filters.indexOf(from)
  var to = filters.find(x => 'date.to' === x.prop)
  var toIndex = filters.indexOf(to)

  return h('div.filter-row', h('div.control.is-grouped.is-horizontal', [
    mkInput(from, fromIndex, update),
    mkInput(to, fromIndex, update)
  ]))
}

export default render
