import {h} from 'virtual-dom'
import moment from 'moment'
import Masonry from 'masonry-layout'
import widget from './widget'
import {hook} from './hook'
import count from './pageviews/count'
import bar from './pageviews/bar'
import pie from './pageviews/pie'
import recommendations from './recommendations/widget'

function render(model, channel) {
  return h('div.container', [
    h('h1.center-h', 'Dashboard'),
    h('div.widgets', {
      hook: hook((node) => {
        new Masonry(node, {
          itemSelector: '.widget',
          columnWidth: 330
        })
      })
    }, [
      widget(recommendations, model, channel),
      widget(bar, model, channel, {groupBy: [], title: 'Pageviews'}),
      widget(count, model, channel, {filterBy: {page: 'page1'}, title: 'Page 1'}),
      widget(count, model, channel, {title: 'All', className: 'red'}),
      widget(count, model, channel, {filterBy: {page: 'page2'}, title: 'Page 2'}),
      widget(pie, model, channel, {
        filterBy: {'date.from': moment().subtract(1, 'year')},
        groupBy: ['date.year', 'user_agent.browser.family'],
        title: 'Browser Statistics Over a Year'
      })
    ])
  ])
}

export default render
