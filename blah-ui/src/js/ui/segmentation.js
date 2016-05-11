import {h} from 'virtual-dom'
import nav from './nav'
import widget from '../widget'
import line from './count/line'
import * as error from './common/error'
import theme from './theme'

function render(model, update, conn, storage) {
  return h('div.container', {
    theme: theme(model)
  }, [
    nav(model, update, conn, storage),
    h('h1.is-text-centered', 'Segmentation'),
    (model.error) ? error.unknown() : widget(line, model, update, {
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
      className: 'size-3of3 segmentation'
    })
  ])
}

export default render
