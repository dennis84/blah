var h = require('snabbdom/h')
var debounce = require('debounce')
var ctrl = require('./ctrl')

function mkOption(filter, key, value, name) {
  return h('option', {
    props: {
      value: value,
      selected: value === filter[key]
    }
  }, name)
}

function mkFilterRow(segment, filter, index, update) {
  var label = index > 2 ? 'And' : 'Filter by'
  return h('div.filter-row', [h('div.control.is-grouped.is-horizontal', [
    h('div.control-label', [h('label.label', label)]),
    h('div.control', [
      h('select.select', {
        on: {change: function(e) {
          update(ctrl.updateSegmentFilter, segment, index, {prop: e.target.value})
        }}
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
        on: {change: function(e) {
          update(ctrl.updateSegmentFilter, segment, index, {operator: e.target.value})
        }}
      }, [
        mkOption(filter, 'operator', '', ''),
        mkOption(filter, 'operator', 'eq', 'Eq'),
        mkOption(filter, 'operator', 'ne', 'Ne'),
        mkOption(filter, 'operator', 'lte', 'Lte'),
        mkOption(filter, 'operator', 'gte', 'Gte')
      ]),
      h('input.input', {
        props: {value: filter.value},
        on: {input: debounce(function(e) {
          update(ctrl.updateSegmentFilter, segment, index, {value: e.target.value})
        }, 500)}
      }),
      h('a.button.is-link', {
        on: {click: function() {
          update(ctrl.removeSegmentFilter, segment, index)
        }}
      }, [h('i.material-icons', 'clear')])
    ])
  ])])
}

function render(segment, update) {
  var filters = segment.filterBy || []
  var nextIndex = filters.length

  return h('div', filters.map(function(filter, index) {
    return mkFilterRow(segment, filter, index, update)
  }).concat([
    mkFilterRow(segment, {
      prop: '',
      operator: '',
      value: ''
    }, nextIndex, update)
  ]))
}

module.exports = render
