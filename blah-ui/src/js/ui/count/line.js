import {h} from 'virtual-dom'
import moment from 'moment'
import {hook, mount} from '../../hook'
import timeframe from '../../timeframe'
import {filterBy, groupBy, form} from '../builder/all'
import {grouped} from './ctrl'
import line from '../chart/line'

function chart(model) {
  var data = timeframe(
    model.groups,
    moment().subtract(1, 'day'),
    moment().add(1, 'hour')
  )

  return h('div.chart', {
    hook: hook((node) => {
      setTimeout(() => {
        node.innerHTML = ''
        return line(node, data)
      }, 0)
    })
  })
}

function render(model, update, conn, options) {
  return h('div.widget.widget-line', {
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
    model.builder ? form(model, update, filterBy, groupBy) : null,
    chart(model)
  ])
}

export default render
