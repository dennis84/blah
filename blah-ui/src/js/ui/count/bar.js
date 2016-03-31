import {h} from 'virtual-dom'
import moment from 'moment'
import {hook, mount} from '../../hook'
import {timeframeD3} from '../../timeframe'
import {filterBy, groupBy, form} from '../builder/all'
import {grouped} from './ctrl'
import bar from '../chart/bar'

function chart(model) {
  var data = timeframeD3(
    model.groups,
    moment().subtract(1, 'day'),
    moment().add(1, 'hour')
  )

  return h('div.chart', {
    hook: hook((node) => {
      setTimeout(() => {
        node.innerHTML = ''
        return bar(node, data)
      }, 0)
    })
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
    model.builder ? form(model, update, filterBy, groupBy) : null,
    chart(model)
  ])
}

export default render
