import {h} from 'virtual-dom'
import widget from './widget'
import count from './pageviews/count'
import grouped from './pageviews/grouped'

function render(model, channel) {
  return h('div.container', [
    h('div.widgets', [
      widget(count, model, channel, {filterBy: {page: 'page1'}}),
      widget(grouped, model, channel, {groupBy: ['user_agent.browser.family']})
    ])
  ])
}

export default render
