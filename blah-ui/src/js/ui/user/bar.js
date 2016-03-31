import {h} from 'virtual-dom'
import {grouped} from './ctrl'
import {hook, mount} from '../../hook'
import Chartist from 'chartist'
import bar from '../chart/bar'

function chart(model, limit = 8) {
  if(!model.users || 0 === model.users.length) return
  var data = model.users.slice(0, limit).map((x) => {
    return {key: x.country, value: x.count}
  })

  return h('div.chart', {
    hook: hook((node) => {
      setTimeout(() => {
        node.innerHTML = ''
        return bar(node, data)
      }, 0)
    })
  })
}

function render(model, update, conn, options) {
  return h('div.widget.widget-bar', {
    className: options.className,
    mount: mount((node) => {
      conn.on('user', (data) => update(grouped, options))
      update(grouped, options)
    })
  }, [
    h('h3', options.title),
    chart(model, options.limit)
  ])
}

export default render
