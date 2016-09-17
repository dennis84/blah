import h from 'snabbdom/h'
import moment from 'moment'
import nav from './nav'
import component from './component'
import * as error from './error'
import container from './container'
import {SERVING_URL} from './../config'

function render(model, update, ws, storage) {
  return container(model, [
    nav(model, update, ws, storage),
    h('h1.has-text-centered', 'Segmentation'),
    (model.error) ? error.unknown() : component(Segmentation, {}, ws, {
      baseUrl: SERVING_URL,
      collection: 'view',
      class: {'size-3of3': true, 'segmentation': true},
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
