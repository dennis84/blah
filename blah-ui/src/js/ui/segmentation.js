import {h} from 'virtual-dom'
import moment from 'moment'
import nav from './nav'
import component from './component'
import * as error from './error'
import theme from './theme'
import {SERVING_URL} from './../config'

function render(model, update, conn, storage) {
  return h('div.container', {
    theme: theme(model)
  }, [
    nav(model, update, conn, storage),
    h('h1.has-text-centered', 'Segmentation'),
    (model.error) ? error.unknown() : component(Segmentation, {}, conn.ws, {
      baseUrl: SERVING_URL,
      collection: 'view',
      className: 'size-3of3 segmentation',
      groups: ['date.year', 'date.month', 'date.day', 'date.hour']
    }, {
      groupBy: ['date.hour'],
      filterBy: [{
        prop: 'date.from',
        operator: 'gte',
        value: moment().subtract(1, 'day').format()
      }, {
        prop: 'date.to',
        operator: 'lte',
        value: moment().add(1, 'hour').format()
      }]
    })
  ])
}

export default render
