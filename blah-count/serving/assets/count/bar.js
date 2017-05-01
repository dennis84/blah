var h = require('snabbdom/h').default
var util = require('../chart/util')
var bar = require('../chart/bar')
var subDays = require('date-fns/sub_days')
var addHours = require('date-fns/add_hours')

function chart(model, options) {
  var dateProps = util.getDateProps(options.filterBy)
  var data = util.timeframe(
    model.groups,
    dateProps.from ? dateProps.from : subDays(Date.now(), 1),
    dateProps.to ? dateProps.to : addHours(Date.now(), 1)
  )

  return h('div.chart', {
    hook: {
      insert: function(vnode) {
        bar(vnode.elm, data)
      },
      update: function(vnode) {
        bar(vnode.elm, data)
      }
    }
  })
}

function render(model, update, options) {
  return h('div.widget.widget-bar', {
    class: options.class
  }, [
    h('h3', options.title),
    chart(model, options)
  ])
}

module.exports = render
