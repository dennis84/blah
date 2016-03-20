import clone from 'clone'
import xtend from 'xtend'

function addFilter(model) {
  var m = clone(model)
  if(undefined === m.builder.filters) {
    m.builder.filterBy = []
    m.builder.filters = []
  }

  m.builder.filterBy.push({
    prop: 'date.from',
    operator: 'eq',
    value: ''
  })

  return m
}

function updateFilter(model, index, prop) {
  var m = clone(model)
  m.builder.filterBy[index] = xtend(m.builder.filterBy[index], prop)
  m.builder.shouldUpdate = true
  return m
}

function updateGroups(model, selected) {
  var m = clone(model)
  var groups = []
  var groupBy = []

  for(var i in m.builder.groups) {
    var group = m.builder.groups[i]
    var index = selected.indexOf(group.value)
    if(-1 !== index) {
      group.selected = true
      groupBy.push(group.value)
    } else {
      group.selected = false
    }

    groups.push(group)
  }

  m.builder.groups = groups
  m.builder.groupBy = groupBy
  m.builder.shouldUpdate = true
  return m
}

export {
  addFilter,
  updateFilter,
  updateGroups
}
