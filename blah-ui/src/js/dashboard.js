import {h} from 'virtual-dom'
import moment from 'moment'
import Masonry from 'masonry-layout'
import widget from './widget'
import {hook} from './hook'
import count from './pageviews/count'
import bar from './pageviews/bar'
import pie from './pageviews/pie'
import recommendations from './recommendations/widget'
import userCount from './user/count'

function render(model, chan, conn) {
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
      widget(pie, model, chan, conn, {
        filterBy: {'date.from': moment().subtract(1, 'year')},
        groupBy: ['date.year', 'user_agent.browser.family'],
        title: 'Browser Statistics Over a Year'
      }),
      widget(bar, model, chan, conn, {
        filterBy: {'date.from': moment().subtract(1, 'day')},
        groupBy: ['date.hour'],
        title: 'Page Views in the past 24 hours',
        className: 'size-2of3'
      }),
      widget(count, model, chan, conn, {title: 'All', className: 'red'}),
      widget(userCount, model, chan, conn, {title: 'Nb Users'}),
      widget(count, model, chan, conn, {filterBy: {page: 'page1'}, title: 'Page 1'}),
      widget(count, model, chan, conn, {filterBy: {page: 'page2'}, title: 'Page 2'}),
      widget(recommendations, model, chan)
    ])
  ])
}

export default render
