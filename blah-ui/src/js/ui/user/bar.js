import {h} from 'virtual-dom'
import {grouped} from './ctrl'
import {hook, mount} from '../../hook'
import Chartist from 'chartist'

function chart(model, limit = 10) {
  if(!model.users || 0 === model.users.length) return
  var labels = model.users.slice(0, limit).map(x => x.country)
  var data = model.users.slice(0, limit).map(x => x.count)

  return h('div.chart', {
    hook: hook((node) => {
      new Chartist.Bar(node, {labels: labels, series: [data]}, {
        fullWidth: true,
        axisX: {showGrid: false},
        axisY: {onlyInteger: true}
      })
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
