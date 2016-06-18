import {h} from 'virtual-dom'
import debounce from 'debounce'
import {updateFilter, removeFilter} from './ctrl'

function mkOption(filter, key, value, name) {
  return h('option', {
    value: value,
    selected: value === filter[key]
  }, name)
}

function mkFilterRow(filter, index, update) {
  var label = index > 2 ? 'And' : 'Filter by'
  return h('div.filter-row', h('div.control.is-grouped.is-horizontal', [
    h('div.control-label', h('label.label', label)),
    h('div.control', [
      h('select.select', {
        onchange: (e) => update(updateFilter, index, {prop: e.target.value})
      }, [
        mkOption(filter, 'prop', '', ''),
        mkOption(filter, 'prop', 'user_agent.device.family', 'Device Family'),
        mkOption(filter, 'prop', 'user_agent.browser.family', 'Browser Family'),
        mkOption(filter, 'prop', 'user_agent.browser.major', 'Browser Major Version'),
        mkOption(filter, 'prop', 'user_agent.os.family', 'OS Family'),
        mkOption(filter, 'prop', 'user_agent.os.major', 'OS Major Version'),
        mkOption(filter, 'prop', 'user_agent.platform', 'Platform')
      ]),
      h('select.select', {
        onchange: (e) => update(updateFilter, index, {operator: e.target.value})
      }, [
        mkOption(filter, 'operator', '', ''),
        mkOption(filter, 'operator', 'eq', 'Eq'),
        mkOption(filter, 'operator', 'ne', 'Ne'),
        mkOption(filter, 'operator', 'lte', 'Lte'),
        mkOption(filter, 'operator', 'gte', 'Gte')
      ]),
      h('input.input', {
        value: filter.value,
        oninput: debounce(e => {
          update(updateFilter, index, {value: e.target.value})
        }, 500)
      }),
      h('a.button.is-link', {
        onclick: e => update(removeFilter, index)
      }, h('i.material-icons', 'clear'))
    ])
  ]))
}

function render(model, update) {
  var filters = model.filterBy || []
  var nextIndex = filters.length
  filters = filters.map((filter, index) => {
    return {value: filter, index: index}
  }).filter(x => 0 !== x.value.prop.indexOf('date.'))

  return h('div', [
    filters.map(x => mkFilterRow(x.value, x.index, update)),
    mkFilterRow({
      prop: '',
      operator: '',
      value: ''
    }, nextIndex, update)
  ])
}

export default render
