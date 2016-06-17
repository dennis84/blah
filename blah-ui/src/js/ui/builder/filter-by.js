import {h} from 'virtual-dom'
import debounce from 'debounce'
import {addFilter, updateFilter} from './ctrl'

function mkFilterRows(model, update) {
  if(undefined === model.filterBy) return
  return model.filterBy.map((filter, index) => h('div.control.is-grouped', [
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
  return h('div', [
    mkFilterRows(model, update),
    h('div.control', h('a.button.is-danger.is-outlined', {
      onclick: (e) => {
        e.preventDefault()
        update(addFilter)
      }
    }, '+ Add Filter')),
  ])
}

export default render
