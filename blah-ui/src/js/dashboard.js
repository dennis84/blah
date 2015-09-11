import {h} from 'virtual-dom'
import widget from './widget'
import count from './pageviews/count'
import grouped from './pageviews/grouped'
import recommendations from './recommendations/widget'

function render(model, channel) {
  return h('div.container', [
    h('div.widgets', [
      widget(count, model, channel, {filterBy: {page: 'page1'}}),
      widget(grouped, model, channel, {groupBy: ['user_agent.browser.family']}),
      widget(recommendations, model, channel, {user: 'user1'})
    ])
  ])
}

export default render
