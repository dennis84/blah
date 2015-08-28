import {h} from 'virtual-dom'
import widget from './widget'
import count from './pageviews/count'
import countAll from './pageviews/count-all'

function render(model, channel) {
  return h('div.container', [
    h('div.widgets', [
      widget(count, model, channel, {event: 'page-1'}),
      widget(count, model, channel, {event: 'page-3'}),
      widget(countAll, model, channel)
    ])
  ])
}

export default render
