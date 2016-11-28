var h = require('snabbdom/h')

function chart(model, limit) {
  if(!model.users || 0 === model.users.length) return []
  var data = model.users.slice(0, limit).map(function(x) {
    return {key: x.country, value: x.count}
  })

  return [h('div.chart', {
    hook: {insert: function(vnode) {
      window.Chart.bar(vnode.elm, data)
    }}
  })]
}

function render(model, update, options) {
  return h('div.widget.widget-bar', {
    class: options.class
  }, [
    h('h3', options.title)
  ].concat(chart(model, options.limit || 8)))
}

module.exports = render
