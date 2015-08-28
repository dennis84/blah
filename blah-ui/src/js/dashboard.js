import {h} from 'virtual-dom'
import widget from './widget'
import pageviews from './pageviews/widget'

function render(model, channel) {
  return h('div.dashboard', [
    h('h1', 'Dashboard ' + model.count),
    h('div.widgets', [
      widget(pageviews, model, channel, {event: 'page-1'}),
      widget(pageviews, model, channel, {event: 'page-3'})
    ])
  ])
}

export default render
