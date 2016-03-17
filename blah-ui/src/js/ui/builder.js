import {h} from 'virtual-dom'
import nav from './nav'
import widget from '../widget'
import builder from './builder/builder'

function render(update, conn, model) {
  return h('div.container', [
    nav(model, update, conn),
    h('h1.center-hv', 'Widget Builder'),
    widget(builder, model, update, {
      title: '',
      type: 'count',
      collection: 'pageviews',
      types: ['count', 'bar', 'pie', 'userCount', 'userBar'],
      collections: ['pageviews'],
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
