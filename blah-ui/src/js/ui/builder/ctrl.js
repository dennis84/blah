import clone from 'clone'
import xtend from 'xtend'

function updateFilter(model, index, prop) {
  var m = clone(model)
  m.filterBy[index] = xtend({
    prop: '',
    operator: '',
    value: ''
  }, m.filterBy[index], prop)
  m.shouldUpdate = true
  return m
}

function removeFilter(model, index) {
  var m = clone(model)
  m.filterBy.splice(index, 1)
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
  updateFilter,
  removeFilter,
  updateGroups
}
