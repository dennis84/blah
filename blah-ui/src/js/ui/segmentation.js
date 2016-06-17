import {h} from 'virtual-dom'
import moment from 'moment'
import nav from './nav'
import widget from '../widget'
import seg from './count/segmentation'
import * as error from './common/error'
import theme from './theme'

function render(model, update, conn, storage) {
  return h('div.container', {
    theme: theme(model)
  }, [
    nav(model, update, conn, storage),
    h('h1.has-text-centered', 'Segmentation'),
    (model.error) ? error.unknown() : widget(seg, model, update, {
      groupBy: ['date.hour'],
      filterBy: [{
        prop: 'date.from',
        operator: 'gte',
        value: moment().subtract(1, 'day').format()
      }]
    }, conn, {
      collection: 'pageviews',
      className: 'size-3of3 segmentation',
      groups: ['date.year', 'date.month', 'date.day', 'date.hour']
    })
  ])
}

export default render
