import {h} from 'virtual-dom'
import widget from './widget'
import pageviews from './pageviews/widget'

function render(model, channel) {
  return h('div.dashboard', [
    h('h1', 'Dashboard ' + model.count),
    h('div.widgets', [
      widget(pageviews, model, channel)
    ])
  ])
}

export default render
