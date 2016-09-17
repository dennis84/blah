var h = require('snabbdom/h')
var ctrl = require('./ctrl')
var flowchart = require('./flowchart')

function content(model, options) {
  if(!model.items || 0 === model.items.length) {
    return h('div.is-empty')
  }

  var items = {}
  for(var i in model.items) {
    var item = model.items[i]
    if(undefined === items[item.item]) {
      items[item.item] = item.count
    } else {
      items[item.item] += item.count
    }
  }

  var data = options.steps.map(function(x) {
    return {key: x, value: items[x]}
  })

  return h('div.chart', {
    hook: {insert: function(vnode) {
      Chart.bar(vnode.elm, data)
    }}
  })
}

function render(model, update, options) {
  return h('div.widget.widget-funnel', {
    class: options.class
  }, [
    h('h3', options.title),
    h('div.tabs', [
      h('ul', [
        h('li', {
          class: {'is-active': 'bar' === model.activeTab}
        }, [h('a', {
          on: {click: function(e) {
            update(ctrl.openTab, 'bar')
          }}
        }, 'Bar Chart')]),
        h('li', {
          class: {'is-active': 'flowchart' === model.activeTab}
        }, [h('a', {
          on: {click: function(e) {
            update(ctrl.openTab, 'flowchart')
          }}
        }, 'Flowchart')])
      ])
    ]),
    'flowchart' === model.activeTab ? flowchart(model, options) : content(model, options)
  ])
}

module.exports = render
