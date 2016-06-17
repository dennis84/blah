import clone from 'clone'
import xtend from 'xtend'

function addFilter(model) {
  var m = clone(model)
  if(undefined === m.filterBy) {
    m.filterBy = []
  }

  m.filterBy.push({
    prop: 'date.from',
    operator: 'eq',
    value: ''
  })

  return m
}

function updateFilter(model, index, prop) {
  var m = clone(model)
  m.filterBy[index] = xtend(m.filterBy[index], prop)
  m.shouldUpdate = true
  return m
}

function updateGroups(model, selected) {
  var m = clone(model)
  m.groupBy = selected
  m.shouldUpdate = true
  return m
}

export {
  addFilter,
  updateFilter,
  updateGroups
}
