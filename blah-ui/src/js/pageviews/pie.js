import {h} from 'virtual-dom'
import Chartist from 'chartist'
import {grouped} from './ctrl'
import hook from '../hook'

function chart(model) {
  if(undefined === model.groups) return
  var data = model.groups.map((x) => x.count)
  var labels = model.groups.map((x) => x.browserFamily)

  return [
    h('div.labels', labels.map((label, i) => {
      return h('div', h('span.label', {
        className: 'label-series-' + String.fromCharCode(i+97)
      }, label))
    })),
    h('div.chart', {
      mount: hook((node) => {
        new Chartist.Pie(node, {series: data}, {
          donut: true,
          donutWidth: 50,
          labelInterpolationFnc: (value) => {
            var sum = (a, b) => a + b
            return Math.round(value / data.reduce(sum) * 100) + '%'
          }
        })
      })
    })
  ]
}

function render(model, update, id, options) {
  return h('div.widget.widget-pie', {
    init: hook((node) => {
      if(null === id) update(grouped, options)
    })
  }, [
    h('h3', options.title),
    chart(model)
  ])
}

export default render
