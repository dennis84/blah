import clone from 'clone'
import xtend from 'xtend'

function setTitle(model, title) {
  var m = clone(model)
  m.title = title
  return m
}

function setType(model, type) {
  var m = clone(model)
  m.type = type
  return m
}

function setCollection(model, collection) {
  var m = clone(model)
  m.collection = collection
  return m
}

function addFilter(model) {
  var m = clone(model)
  if(undefined === m.filters) m.filters = []
  m.filters.push({
    prop: 'date.from',
    operator: 'eq',
    value: '',
    props: ['date.from', 'date.to'],
    operators: ['eq', 'lte', 'gte']
  })

  return m
}

function updateFilter(model, index, prop) {
  var m = clone(model)
  m.filters[index] = xtend(m.filters[index], prop)
  return m
}

function updateGroups(model, selected) {
  var m = clone(model)
  var groups = []
  for(var i in m.groups) {
    var group = m.groups[i]
    var index = selected.indexOf(group.value)
    if(-1 !== index) {
      group.selected = true
    } else {
      group.selected = false
    }

    groups.push(group)
  }

  m.groups = groups
  return m
}

function createWidget(model) {
  var widgets = window.localStorage.getItem('widgets')
  if(widgets) widgets = JSON.parse(widgets)
  else widgets = []

  widgets.push({
    title: model.title,
    collection: model.collection,
    type: model.type,
    filterBy: model.filters,
    groupBy: model.groups.filter((x) => x.selected).map((x) => x.value)
  })

  window.localStorage.setItem('widgets', JSON.stringify(widgets))

  return model
}

export {
  setTitle,
  setType,
  setCollection,
  addFilter,
  updateFilter,
  updateGroups,
  createWidget
}
