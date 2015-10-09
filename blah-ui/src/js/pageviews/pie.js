import {h} from 'virtual-dom'
import Chartist from 'chartist'
import {grouped} from './ctrl'
import {hook, mount} from '../hook'

function chart(model) {
  if(undefined === model.groups) return
  var data = model.groups.map((x) => x.count)
  var labels = model.groups.map((x) => x.browserFamily)

  return [
    h('div.labels', labels.map((label, i) => {
      return h('span.label', {
        className: 'label-series-' + String.fromCharCode(i + 97)
      }, label)
    })),
    h('div.chart', {
      hook: hook((node) => {
        new Chartist.Pie(node, {series: data}, {
          donut: true,
          donutWidth: 50,
          labelInterpolationFnc: (value) => {
            return Math.round(value / data.reduce((a,b) => a + b) * 100) + '%'
          }
        })
      })
    })
  ]
}

function render(model, update, conn, options) {
  return h('div.widget.widget-pie', {
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
