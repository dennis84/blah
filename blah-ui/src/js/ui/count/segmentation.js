import {h} from 'virtual-dom'
import moment from 'moment'
import xtend from 'xtend'
import {hook, mount} from '../../hook'
import timeframe from '../../timeframe'
import {filterBy, groupBy, form} from '../builder/all'
import {grouped} from './ctrl'
import line from '../chart/line'

function chart(model) {
  var from = model.filterBy
    .find(x => 'date.from' === x.prop)
  var to = model.filterBy
    .find(x => 'date.to' === x.prop)
  from = from ? moment(from.value) : moment().subtract(1, 'day'),
  to = to ? moment(to.value) : moment().add(1, 'hour')
  var diff = to.diff(from, 'days');
  var format = 'ddd h:mm a'
  if(diff > 2) format = 'ddd M'
  if(diff > 7) format = 'MMM DD'
  if(diff > 60) format = 'MMM YYYY'

  var data = timeframe(model.groups, from, to, {format: format})

  return h('div.chart', {
    hook: hook(node => line(node, data))
  })
}

function render(model, update, conn, options) {
  return h('div', [
    form(model, update, filterBy, groupBy(options)),
    h('div.widget.widget-line', {
      className: options.className,
      mount: mount(node => update(grouped, xtend(options, model))),
      hook: hook(node => {
        conn.off('count').on('count', data => {
          update(grouped, xtend(options, model))
        })

        if(model.shouldUpdate) {
          update(grouped, xtend(options, model))
          model.shouldUpdate = false
        }
      })
    }, [
      h('h3', options.title),
      chart(model)
    ])
  ])
}

export default render
