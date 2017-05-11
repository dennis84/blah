var h = require('snabbdom/h').default
var chart = require('./chart')

function renderChart(model, limit) {
  if(!model.users || 0 === model.users.length) return []
  var data = model.users.slice(0, limit).map(function(x) {
    return {key: x.country, value: x.count}
  })

  return [h('div.chart', {
    hook: {insert: function(vnode) {
      chart(vnode.elm, data)
    }}
  })]
}

function render(model, update, options) {
  return h('div.user-bar.box.widget', {
    class: options.class
  }, [
    h('h3', options.title)
  ].concat(renderChart(model, options.limit || 8)))
}

module.exports = render
