var clone = require('clone')
var xtend = require('xtend')

function updateFilter(model, index, prop) {
  var m = clone(model)
  m.filterBy[index] = xtend({
    prop: '',
    operator: '',
    value: ''
  }, m.filterBy[index], prop)
  return m
}

function removeFilter(model, index) {
  var m = clone(model)
  m.filterBy.splice(index, 1)
  return m
}

function updateGroups(model, selected) {
  var m = clone(model)
  m.groupBy = selected
  return m
}

module.exports = {
  updateFilter: updateFilter,
  removeFilter: removeFilter,
  updateGroups: updateGroups
}
