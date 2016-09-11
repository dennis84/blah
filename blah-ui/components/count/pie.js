var h = require('snabbdom/h')
var clone = require('clone')

function chart(model) {
  if(!model.groups || 0 === model.groups.length) return []
  var data = model.groups.map(function(x) {
    var y = clone(x)
    delete y['count']
    delete y['date']
    return {key: Object.values(y).join(', '), value: x.count}
  })

  return [
    h('div.has-text-centered', data.map(function(d,i) {
      return h('span.tag', {
        props: {className: 'is-colored-' + String.fromCharCode(i + 97)}
      }, d.key)
    })),
    h('div.chart', {
      hook: {
        insert: function(vnode) {
          Chart.donut(vnode.elm, data)
        }
      }
    })
  ]
}

function render(model, update, options) {
  return h('div.widget.widget-pie', {
    props: {className: options.className}
  }, [
    h('h3', options.title)
  ].concat(chart(model)))
}

module.exports = render
