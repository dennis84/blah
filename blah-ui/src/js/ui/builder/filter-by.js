import {h} from 'virtual-dom'
import debounce from 'debounce'
import {addFilter, updateFilter} from './ctrl'

function mkFilterRow(filter, index, update) {
  return h('div.filter-row', h('div.control.is-grouped', [
    h('select.select', {
      onchange: (e) => update(updateFilter, index, {prop: e.target.value})
    }, [
      h('option', {
        value: 'date.from',
        selected: 'date.from' === filter.prop
      }, 'From'),
      h('option', {
        value: 'date.to',
        selected: 'date.to' === filter.prop
      }, 'To'),
    ]),
    h('select.select', {
      onchange: (e) => update(updateFilter, index, {operator: e.target.value})
    }, [
      h('option', {
        value: 'eq',
        selected: 'eq' === filter.operator
      }, 'eq'),
      h('option', {
        value: 'lte',
        selected: 'lte' === filter.operator
      }, 'lte'),
      h('option', {
        value: 'gte',
        selected: 'gte' === filter.operator
      }, 'gte')
    ]),
    h('input.input', {
      value: filter.value,
      oninput: debounce((e) => {
        update(updateFilter, index, {value: e.target.value})
      }, 500)
    })
  ]))
}

function render(model, update) {
  var filters = model.filterBy || []
  return h('div', [
    filters.map((filter, index) => mkFilterRow(filter, index, update)),
    mkFilterRow({}, filters.length, update)
  ])
}

export default render
