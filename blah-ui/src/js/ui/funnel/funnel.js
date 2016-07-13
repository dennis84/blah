import {h} from 'virtual-dom'
import {search, openTab} from './ctrl'
import {hook, mount} from '../../hook'
import bar from '../chart/bar'
import flowchart from './flowchart'

function content(model, options) {
  if(!model.items || 0 === model.items.length) return
  var items = {}
  for(var i in model.items) {
    var item = model.items[i]
    if(undefined === items[item.item]) {
      items[item.item] = item.count
    } else {
      items[item.item] += item.count
    }
  }

  var data = options.steps.map(x => {
    return {key: x, value: items[x]}
  })

  return h('div.chart', {
    hook: hook((node) => bar(node, data))
  })
}

function render(model, update, conn, options) {
  return h('div.widget.widget-funnel', {
    className: options.className,
    mount: mount((node) => {
      conn.on('funnel', (data) => update(search, options))
      update(search, options)
    })
  }, [
    h('h3', options.title),
    h('div.tabs', [
      h('ul', [
        h('li', {
          className: 'flowchart' !== model.activeTab ? 'is-active' : ''
        }, h('a', {
          onclick: e => update(openTab, 'bar')
        }, 'Bar Chart')),
        h('li', {
          className: 'flowchart' === model.activeTab ? 'is-active' : ''
        }, h('a', {
          onclick: e => update(openTab, 'flowchart')
        }, 'Flowchart'))
      ])
    ]),
    'flowchart' === model.activeTab ? flowchart(model, options) : content(model, options)
  ])
}

export default render
