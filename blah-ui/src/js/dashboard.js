import {h} from 'virtual-dom'
import Masonry from 'masonry-layout'
import widget from './widget'
import hook from './hook'
import count from './pageviews/count'
import grouped from './pageviews/grouped'
import recommendations from './recommendations/widget'

function render(model, channel) {
  console.log(Masonry)
  return h('div.container', [
    h('div.widgets', {
      mount: hook(function(node) {
        new Masonry(node, {
          itemSelector: '.widget',
          columnWidth: 220
        })
      })
    }, [
      widget(count, model, channel, {filterBy: {page: 'page1'}}),
      widget(grouped, model, channel, {groupBy: ['user_agent.browser.family']}),
      widget(recommendations, model, channel, {user: 'user1'}),
      widget(count, model, channel, {filterBy: {page: 'page2'}})
    ])
  ])
}

export default render
