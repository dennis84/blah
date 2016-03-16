import {h} from 'virtual-dom'
import nav from './nav'
import widget from '../widget'
import builder from './builder/builder'

function render(update, conn, model) {
  return h('div.container', [
    nav(model, update, conn),
    h('h1.center-hv', 'Custom'),
    widget(builder, model, update, {
      title: '',
      type: 'count',
      collection: 'pageviews',
      types: ['count', 'bar', 'pie'],
      collections: ['pageviews', 'purchases'],
      filters: [],
      groups: [
        {value: 'date.year', selected: true},
        {value: 'date.month', selected: false},
        {value: 'date.day', selected: false}
      ]
    })
  ])
}

export default render
