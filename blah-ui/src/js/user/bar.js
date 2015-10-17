import {h} from 'virtual-dom'
import {grouped} from './ctrl'
import {hook, mount} from '../hook'
import Chartist from 'chartist'

function chart(model) {
  if(undefined === model.users) return
  var labels = model.users.map((x) => x.country)
  var data = model.users.map((x) => x.count)

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
    mount: mount((node) => update(grouped, options))
  }, [
    h('h3', options.title),
    chart(model)
  ])
}

export default render
