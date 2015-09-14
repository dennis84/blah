import {h} from 'virtual-dom'
import Chartist from 'chartist'
import {grouped} from './ctrl'
import hook from '../hook'

function chart(model) {
  if(undefined === model.groups) return
  var data = model.groups.map((x) => x.count)
  var labels = model.groups.map((x) => x.browserFamily)
  return h('div.chart', {
    mount: hook((node) => {
      new Chartist.Pie(node, {
        series: data,
        labels: labels
      }, {
        donut: true,
        donutWidth: 50
      })
    })
  })
}

function render(model, update, id, options) {
  return h('div.widget.widget-pie', {
    init: hook((node) => {
      if(null === id) update(grouped, options)
    })
  }, chart(model))
}

export default render
