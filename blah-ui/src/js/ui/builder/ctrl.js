import clone from 'clone'
import xtend from 'xtend'

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
  updateFilter,
  updateGroups
}
