import {h, create} from 'virtual-dom'
import parse from 'form-parse'
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
  return h('input', {
    name: 'title',
    value: model.title,
    oninput: debounce((e) => update(setTitle, e.target.value), 500)
  })
}

function type(model, update) {
  return h('select', {
    name: 'type',
    onchange: (e) => update(setType, e.target.value),
  }, model.types.map((type) => {
    return h('option', {value: type}, type)
  }))
}

function collection(model, update) {
  return h('select', {
    name: 'collection',
    onchange: (e) => update(setCollection, e.target.value),
  }, model.collections.map((coll) => {
    return h('option', {value: coll}, coll)
  }))
}

function filters(model, update) {
  return model.filters.map((filter, index) => h('div', [
    h('select', {
      onchange: (e) => update(updateFilter, index, {prop: e.target.value})
    }, filter.props.map((prop) => h('option', prop))),
    h('select', {
      onchange: (e) => update(updateFilter, index, {operator: e.target.value})
    }, filter.operators.map((operator) => h('option', operator))),
    h('input', {
      oninput: debounce((e) => update(updateFilter, index, {value: e.target.value}), 500)
    })
  ]))
}

function groups(model, update) {
  return h('select', {
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
}

function render(model, update, conn) {
  return h('form', [
    h('div', title(model, update)),
    h('div', type(model, update)),
    h('div', collection(model, update)),
    h('div', filters(model, update)),
    h('div', groups(model, update)),
    h('button.button-red', {
      onclick: (e) => {
        e.preventDefault()
        update(addFilter)
      }
    }, '+ Add Filter'),
    h('a.button-red-filled', {
      onclick: (e) => {
        e.preventDefault()
        update(createWidget)
      }
    }, 'Create')
  ])
}

export default render
