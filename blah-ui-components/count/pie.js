var h = require('snabbdom/h')
var clone = require('clone')

function chart(model) {
  if(!model.groups || 0 === model.groups.length) return []
  var data = model.groups.map(function(x) {
    var y = clone(x)
    delete y['count']
    delete y['date']

    var values = Object.keys(y).map(function(key) {
      return y[key]
    })

    return {key: values.join(', '), value: x.count}
  })

  return [
    h('div.has-text-centered', data.map(function(d,i) {
      var classAttrs = {}
      classAttrs['is-colored-' + String.fromCharCode(i + 97)] = true
      return h('span.tag', {class: classAttrs}, d.key)
    })),
    h('div.chart', {
      hook: {
        insert: function(vnode) {
          window.Chart.donut(vnode.elm, data)
        },
        update: function(vnode) {
          window.Chart.donut(vnode.elm, data)
        }
      }
    })
  ]
}

function render(model, update, options) {
  return h('div.widget.widget-pie', {
    class: options.class
  }, [
    h('h3', options.title)
  ].concat(chart(model)))
}

module.exports = render
