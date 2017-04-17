var h = require('snabbdom/h').default
var util = require('../chart/util')
var line = require('../chart/line')
var subDays = require('date-fns/sub_days')
var addHours = require('date-fns/add_hours')

function chart(model) {
  var data = util.timeframe(
    model.groups,
    subDays(Date.now(), 1),
    addHours(Date.now(), 1)
  )

  return h('div.chart', {
    hook: {
      insert: function(vnode) {
        line(vnode.elm, data)
      },
      update: function(vnode) {
        line(vnode.elm, data)
      }
    }
  })
}

function render(model, update, options) {
  return h('div.widget.widget-line', {
    class: options.class
  }, [
    h('h3', options.title),
    chart(model)
  ])
}

module.exports = render
