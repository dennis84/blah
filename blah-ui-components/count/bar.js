var h = require('snabbdom/h')
var moment = require('moment')

function chart(model) {
  var data = window.Chart.timeframe(
    model.groups,
    moment().subtract(1, 'day'),
    moment().add(1, 'hour')
  )

  return h('div.chart', {
    hook: {
      insert: function(vnode) {
        window.Chart.bar(vnode.elm, data)
      },
      update: function(vnode) {
        window.Chart.bar(vnode.elm, data)
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
