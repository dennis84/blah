import {h} from 'virtual-dom'
import moment from 'moment'
import Masonry from 'masonry-layout'
import debounce from 'debounce'
import widget from './widget'
import {hook} from './hook'
import count from './pageviews/count'
import bar from './pageviews/bar'
import pie from './pageviews/pie'
import recommendations from './recommendations/widget'
import userCount from './user/count'
import userBar from './user/bar'

function render(model, chan, conn) {
  return h('div.container', [
    h('h1.center-h', 'Dashboard'),
    h('div.widgets', {
      hook: hook(debounce((node) => {
        if(undefined === node.masonry) {
          node.masonry = new Masonry(node, {
            itemSelector: '.widget'
          })
        } else {
          node.masonry.reloadItems()
          node.masonry.layout()
        }
      }), 100)
    }, [
      widget(pie, model, chan, conn, {
        filterBy: [{
          prop: 'date.from',
          operator: 'gte',
          value: moment().subtract(1, 'year')
        }],
        groupBy: ['date.year', 'user_agent.browser.family'],
        title: 'Browser Statistics Over a Year'
      }),
      widget(bar, model, chan, conn, {
        filterBy: [{
          prop: 'date.from',
          operator: 'gte',
          value: moment().subtract(1, 'day')
        }],
        groupBy: ['date.hour'],
        title: 'Page Views in the past 24 hours',
        className: 'size-2of3'
      }),
      widget(userCount, model, chan, conn, {filterBy: [{
        prop: 'date.from',
        operator: 'gte',
        value: moment().subtract(1, 'day')
      }], title: 'Number of visitors today'}),
      widget(userBar, model, chan, conn, {
        groupBy: ['country'],
        title: 'Visitors by Country'
      }),
      widget(userCount, model, chan, conn, {title: 'Unique Visitors'}),
      widget(count, model, chan, conn, {title: 'All', className: 'red'}),
      widget(pie, model, chan, conn, {
        filterBy: [{
          prop: 'date.from',
          operator: 'gte',
          value: moment().subtract(1, 'year')
        }],
        groupBy: ['date.year', 'user_agent.platform'],
        title: 'Platform Statistics'
      }),
      widget(count, model, chan, conn, {filterBy: [{
        prop: 'page',
        operator: 'eq',
        value: 'page-1'
      }], title: 'Page 1'}),
      widget(pie, model, chan, conn, {
        filterBy: [{
          prop: 'date.from',
          operator: 'gte',
          value: moment().subtract(1, 'year')
        }, {
          prop: 'user_agent.platform',
          operator: 'eq',
          value: 'Mobile'
        }],
        groupBy: ['date.year', 'user_agent.os.family'],
        title: 'Mobile Operating Systems'
      }),
      widget(count, model, chan, conn, {filterBy: [{
        prop: 'page',
        operator: 'eq',
        value: 'page-2'
      }], title: 'Page 2'}),
      widget(recommendations, model, chan)
    ])
  ])
}

export default render
