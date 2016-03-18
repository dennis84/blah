import {h} from 'virtual-dom'
import debounce from 'debounce'
import {addFilter, updateFilter} from './ctrl'

function mkFilterRows(model, update) {
  if(undefined === model.filterBy) return
  return model.filterBy.map((filter, index) => h('div.control', [
    h('select.select', {
      onchange: (e) => {
        update(updateFilter, index, {prop: e.target.value})
      }
    }, [
      h('option', {value: 'date.from'}, 'From'),
      h('option', {value: 'date.to'}, 'To'),
    ]),
    h('select.select', {
      onchange: (e) => {
        update(updateFilter, index, {operator: e.target.value})
      }
    }, [
      h('option', {value: 'eq'}, 'eq'),
      h('option', {value: 'lte'}, 'lte'),
      h('option', {value: 'gte'}, 'gte')
    ]),
    h('input.input-text', {
      oninput: debounce((e) => {
        update(updateFilter, index, {value: e.target.value})
      }, 500)
    })
  ]))
}

function render(model, update, ctrlFn, options) {
  return h('div', [
    mkFilterRows(model, update, ctrlFn),
    h('div.control', h('a.button.is-red', {
      onclick: (e) => {
        e.preventDefault()
        update(addFilter)
      }
    }, '+ Add Filter')),
  ])
}

export default render
