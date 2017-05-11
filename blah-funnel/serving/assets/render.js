var h = require('snabbdom/h').default
var ctrl = require('./ctrl')
var tree = require('./tree')
var bar = require('./bar')

function chart(model, options) {
  if(!model.items || 0 === model.items.length) {
    return h('div.is-empty')
  }

  if('tree' === model.activeTab) {
    return h('div.chart.tree', {
      hook: {
        insert: function(vnode) {
          tree(vnode.elm, model.items)
        }
      }
    })
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
      bar(vnode.elm, data)
    }}
  })
}

function toggle(model, update) {
  if('bar' === model.activeTab) {
    return h('div.control', [
      h('span.tag.is-danger', 'New!'),
      h('a.button.is-link', {
        on: {click: function() {
          update(ctrl.openTab, 'tree')
        }}
      }, 'Display funnel as tree chart')
    ])
  }

  return h('div.control', [
    h('a.button.is-link', {
      on: {click: function() {
        update(ctrl.openTab, 'bar')
      }}
    }, 'Display funnel as bar chart')
  ])
}

function render(model, update, options) {
  return h('div.funnel', [
    h('h1.title.is-1.has-text-centered', options.title ? options.title : 'Funnel'),
    h('div.box', [
      chart(model, options),
      toggle(model, update)
    ])
  ])
}

module.exports = render
