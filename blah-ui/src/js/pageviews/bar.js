import {h} from 'virtual-dom'
import {grouped} from './ctrl'
import {hook, mount} from '../hook'
import Chartist from 'chartist'
import moment from 'moment'

function chart(model) {
  if(undefined === model.groups) return
  var labels = model.groups.map((x) => moment(x.date).format('ll'))
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

function render(model, update, options) {
  return h('div.widget.widget-bar', {
    mount: mount((node) => update(grouped, options))
  }, [
    h('h3', options.title),
    chart(model)
  ])
}

export default render
