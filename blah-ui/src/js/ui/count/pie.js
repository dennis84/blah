import {h} from 'virtual-dom'
import clone from 'clone'
import {grouped} from './ctrl'
import {hook, mount} from '../../hook'
import donut from '../chart/donut'
import * as error from '../common/error'

function chart(model) {
  if(!model.groups || 0 === model.groups.length) return
  var data = model.groups.map(x => {
    var y = clone(x)
    delete y['count']
    delete y['date']
    return {key: Object.values(y).join(', '), value: x.count}
  })

  return [
    h('div.is-text-centered', data.map((d,i) => {
      return h('span.tag', {
        className: 'is-colored-' + String.fromCharCode(i + 97)
      }, d.key)
    })),
    h('div.chart', {
      hook: hook(node => donut(node, data))
    })
  ]
}

function render(model, update, conn, options) {
  return h('div.widget.widget-pie', {
    className: options.className,
    mount: mount(node => {
      conn.on('count', (data) => update(grouped, options))
      update(grouped, options)
    })
  }, [
    h('h3', options.title),
    chart(model)
  ])
}

export default render
