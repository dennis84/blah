import {h} from 'virtual-dom'
import moment from 'moment'
import {hook, mount} from '../../hook'
import timeframe from '../../timeframe'
import {grouped} from './ctrl'
import line from '../chart/line'

function chart(model) {
  var data = timeframe(
    model.groups,
    moment().subtract(1, 'day'),
    moment().add(1, 'hour')
  )

  return h('div.chart', {
    hook: hook(node => line(node, data))
  })
}

function render(model, update, conn, options) {
  return h('div.widget.widget-line', {
    className: options.className,
    mount: mount(node => update(grouped, options)),
    hook: hook(node => {
      conn.off('count').on('count', data => update(grouped, options))
    })
  }, [
    h('h3', options.title),
    chart(model)
  ])
}

export default render
