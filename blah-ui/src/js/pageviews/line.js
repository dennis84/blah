import {h} from 'virtual-dom'
import {grouped} from './ctrl'
import hook from '../hook'
import Chartist from 'chartist'

function chart(model) {
  if(undefined === model.groups) return
  var labels = model.groups.map((x) => x.date)
  var data = model.groups.map((x) => x.count)

  return h('div.chart', {
    mount: hook((node) => {
      new Chartist.Line(node, {labels: labels, series: [data]}, {
        fullWidth: true,
        axisY: {showGrid: false}
      })
    })
  })
}

function render(model, update, id, options) {
  return h('div.widget.widget-line', {
    init: hook((node) => {
      if(null === id) update(grouped, options)
    })
  }, chart(model))
}

export default render
