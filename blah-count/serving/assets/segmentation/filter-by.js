var h = require('snabbdom/h').default
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
  var isFirst = index === 0
  return h('div.control.is-horizontal', [
    h('div.control-label', [
      h('label.label', isFirst ? 'Filter by' : 'And')
    ]),
    h('div.control.is-grouped', [
      h('div.control.is-expanded', [
        h('div.select.is-fullwidth', [
          h('select', {
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
          ])
        ])
      ]),
      h('div.control.is-expanded', [
        h('div.select.is-fullwidth', [
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
          ])
        ])
      ]),
      h('div.control.is-expanded', [
        h('input.input', {
          props: {value: filter.value},
          on: {input: debounce(function(e) {
            update(ctrl.updateSegmentFilter, segment, index, {value: e.target.value})
          }, 500)}
        })
      ]),
      h('a.button.is-link', {
        on: {click: function() {
          update(ctrl.removeSegmentFilter, segment, index)
        }}
      }, [h('i.material-icons.is-small', 'clear')])
    ])
  ])
}

function render(segment, update) {
  var filters = segment.filterBy || []
  return h('div.card-content', filters.map(function(filter, index) {
    return mkFilterRow(segment, filter, index, update)
  }))
}

module.exports = render
