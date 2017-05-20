var h = require('snabbdom/h').default
var clone = require('clone')
var donut = require('../chart/donut')

function chart(model) {
  var groups = model.groups
  var isEmpty = false
  if(!groups || !groups.length) {
    groups = [{count: 100}]
    isEmpty = true
  }

  var data = groups.map(function(x) {
    var y = clone(x)
    delete y['count']
    delete y['date']

    var values = Object.keys(y).map(function(key) {
      return y[key]
    })

    return {key: values.join(', '), value: x.count}
  })

  return [
    h('div.has-text-centered', data.map(function(d, i) {
      var classAttrs = {}
      classAttrs['is-color-' + (i + 1)] = true
      classAttrs['is-empty'] = isEmpty
      return h('span.tag', {class: classAttrs}, d.key)
    })),
    h('div.chart', {
      class: {'is-empty': isEmpty},
      hook: {
        insert: function(vnode) {
          donut(vnode.elm, data)
        },
        update: function(vnode) {
          donut(vnode.elm, data)
        }
      }
    })
  ]
}

function render(model, update, options) {
  return h('div.count-pie.box.widget', {
    class: options.class
  }, [
    h('h3', options.title)
  ].concat(chart(model)))
}

module.exports = render
