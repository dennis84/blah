var h = require('snabbdom/h').default
var moment = require('moment')
var timeframe = require('../chart/timeframe')
var bar = require('../chart/bar')

function chart(model) {
  var data = timeframe(
    model.groups,
    moment().subtract(1, 'day'),
    moment().add(1, 'hour')
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
    chart(model)
  ])
}

module.exports = render
