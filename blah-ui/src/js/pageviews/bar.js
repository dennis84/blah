import {h} from 'virtual-dom'
import {grouped} from './ctrl'
import {hook, mount} from '../hook'
import Chartist from 'chartist'
import moment from 'moment'

function chart(model) {
  if(undefined === model.groups) return
  var labels = model.groups.map((x) => moment(x.date).format('h:mm:ss a'))
  var data = model.groups.map((x) => x.count)

  return h('div.chart', {
    hook: hook((node) => {
      new Chartist.Bar(node, {labels: labels, series: [data]}, {
        fullWidth: true,
        axisX: {showGrid: false}
      })
    })
  })
}

function render(model, update, conn, options) {
  return h('div.widget.widget-bar', {
    className: options.className,
    mount: mount((node) => {
      conn.on('count', (data) => update(grouped, options))
      update(grouped, options)
    })
  }, [
    h('h3', options.title),
    chart(model)
  ])
}

export default render
