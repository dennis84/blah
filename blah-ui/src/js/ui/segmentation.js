import {h} from 'virtual-dom'
import nav from './nav'
import widget from '../widget'
import line from './count/line'
import error from './common/error'

function render(update, conn, model) {
  return h('div.container', [
    nav(model, update, conn),
    h('h1.center-hv', 'Segmentation'),
    error(model) || widget(line, model, update, {
      builder: {
        groups: [
          {value: 'date.year', selected: false},
          {value: 'date.month', selected: false},
          {value: 'date.day', selected: false},
          {value: 'date.hour', selected: false}
        ]
      }
    }, conn, {
      collection: 'pageviews',
      groupBy: ['date.hour'],
      className: 'size-3of3'
    })
  ])
}

export default render
