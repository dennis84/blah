import {h, create} from 'virtual-dom'
import debounce from 'debounce'
import nav from '../nav'
import {
  setTitle,
  setType,
  setCollection,
  addFilter,
  updateFilter,
  updateGroups,
  createWidget
} from './ctrl'

function title(model, update) {
  return h('div.control', [
    h('label', 'Widget Title'),
    h('input.input-text', {
      name: 'title',
      value: model.title,
      oninput: debounce((e) => update(setTitle, e.target.value), 500)
    })
  ])
}

function type(model, update) {
  return h('div.control', [
    h('label', 'Widget Type'),
    h('select.select', {
      name: 'type',
      onchange: (e) => update(setType, e.target.value),
    }, model.types.map((type) => {
      return h('option', {value: type}, type)
    }))
  ])
}

function collection(model, update) {
  return h('div.control', [
    h('label', 'Collection'),
    h('select.select', {
      name: 'collection',
      onchange: (e) => update(setCollection, e.target.value),
    }, model.collections.map((coll) => {
      return h('option', {value: coll}, coll)
    }))
  ])
}

function filters(model, update) {
  return model.filters.map((filter, index) => h('div.control', [
    h('select.select', {
      onchange: (e) => update(updateFilter, index, {prop: e.target.value})
    }, filter.props.map((prop) => h('option', prop))),
    h('select.select', {
      onchange: (e) => update(updateFilter, index, {operator: e.target.value})
    }, filter.operators.map((operator) => h('option', operator))),
    h('input.select', {
      oninput: debounce((e) => update(updateFilter, index, {value: e.target.value}), 500)
    })
  ]))
}

function groups(model, update) {
  return h('div.control', [
    h('label', 'Collection'),
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

function render(model, update) {
  return h('form', [
    title(model, update),
    type(model, update),
    collection(model, update),
    filters(model, update),
    groups(model, update),
    h('div.control', h('a.button.is-red', {
      onclick: (e) => {
        e.preventDefault()
        update(addFilter)
      }
    }, '+ Add Filter')),
    h('div.control', h('a.button.is-red.is-filled', {
      onclick: (e) => {
        e.preventDefault()
        update(createWidget)
      }
    }, 'Save'))
  ])
}

export default render
