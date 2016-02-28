import {h} from 'virtual-dom'
import Chartist from 'chartist'
import {search} from './ctrl'
import {hook, mount} from '../../hook'

function content(model, options) {
  if(undefined === model.steps || 0 === model.steps.length) return
  var steps = model.steps.sort((a, b) => {
    return a.path.length > b.path.length
  })

  var labels = steps.map(x => x.path.join(" > "))
  var data = steps.map(x => x.count)

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
  return h('div.widget.widget-funnel', {
    className: options.className,
    mount: mount((node) => {
      conn.on('funnel', (data) => update(search, options))
      update(search, options)
    })
  }, [
    h('h3', options.title),
    content(model, options)
  ])
}

export default render
