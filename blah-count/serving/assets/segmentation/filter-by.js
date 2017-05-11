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
  return h('div.field.is-horizontal', [
    h('div.field-label.is-medium', [
      h('label.label', isFirst ? 'Filter by' : 'And')
    ]),
    h('div.field-body', [
      h('div.field.is-grouped', [
        h('p.control.is-expanded', [
          h('div.select.is-fullwidth.is-medium', [
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
        ])
      ]),
      h('div.field.is-grouped', [
        h('p.control.is-expanded', [
          h('div.select.is-fullwidth.is-medium', [
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
        ])
      ]),
      h('div.field.is-grouped', [
        h('p.control.is-expanded', [
          h('input.input.is-medium', {
            props: {value: filter.value},
            on: {input: debounce(function(e) {
              update(ctrl.updateSegmentFilter, segment, index, {value: e.target.value})
            }, 500)}
          })
        ])
      ]),
      h('div.field.is-grouped.is-narrow', [
        h('p.control.is-expanded', [
          h('a.button.is-danger.is-outlined.is-medium', {
            on: {click: function() {
              update(ctrl.removeSegmentFilter, segment, index)
            }}
          }, [h('i.material-icons.is-small', 'clear')])
        ])
      ])
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
