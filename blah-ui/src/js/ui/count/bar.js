import {h} from 'virtual-dom'
import moment from 'moment'
import {hook, mount} from '../../hook'
import timeframe from '../../timeframe'
import {grouped} from './ctrl'
import bar from '../chart/bar'

function chart(model) {
  var data = timeframe(
    model.groups,
    moment().subtract(1, 'day'),
    moment().add(1, 'hour')
  )

  return h('div.chart', {
    hook: hook(node => bar(node, data))
  })
}

function render(model, update, conn, options) {
  return h('div.widget.widget-bar', {
    className: options.className,
    mount: mount((node) => {
      conn.on('count', (data) => update(grouped, options))
      update(grouped, options)
    }),
    hook: hook((node) => {
      if(model.builder && model.builder.shouldUpdate) {
        update(grouped, options)
        model.builder.shouldUpdate = false
      }
    })
  }, [
    h('h3', options.title),
    chart(model)
  ])
}

export default render
