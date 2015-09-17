import {h} from 'virtual-dom'
import moment from 'moment'
import Masonry from 'masonry-layout'
import widget from './widget'
import hook from './hook'
import count from './pageviews/count'
import line from './pageviews/line'
import pie from './pageviews/pie'
import recommendations from './recommendations/widget'

function render(model, channel) {
  return h('div.container', [
    h('div.widgets', {
      mount: hook((node) => {
        new Masonry(node, {
          itemSelector: '.widget',
          columnWidth: 220
        })
      })
    }, [
      widget(count, model, channel, {filterBy: {page: 'page1'}, title: 'Page 1'}),
      widget(count, model, channel, {title: 'All'}),
      widget(line, model, channel, {groupBy: ['user_agent.browser.family']}),
      widget(recommendations, model, channel, {user: 'user1'}),
      widget(count, model, channel, {filterBy: {page: 'page2'}, title: 'Page 2'}),
      widget(pie, model, channel, {filterBy: {
        'date.from': moment('2014', 'YYYY').utc().startOf('day').format()
      }, groupBy: ['user_agent.browser.family']})
    ])
  ])
}

export default render
