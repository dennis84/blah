import moment from 'moment'
import widget from '../widget'
import count from './count/count'
import countDiff from './count/count-diff'
import bar from './count/bar'
import pie from './count/pie'
import recommendations from './recommendations/widget'
import userCount from './user/count'
import userBar from './user/bar'
import sum from './sum/sum'
import funnel from './funnel/funnel'

function makeWidgets(model, chan, conn) {
  return [
    // --------------------------------------------------------------
    // Pie Chart: Browser Statistics Over a Year
    // --------------------------------------------------------------
    widget(pie, model, chan, conn, {
      collection: 'pageviews',
      filterBy: [{
        prop: 'date.from',
        operator: 'gte',
        value: moment().subtract(1, 'year')
      }],
      groupBy: ['date.year', 'user_agent.browser.family'],
      title: 'Browser Statistics Over a Year'
    }),

    // --------------------------------------------------------------
    // Bar Chart: Page Views in the past 24 hours
    // --------------------------------------------------------------
    widget(bar, model, chan, conn, {
      collection: 'pageviews',
      filterBy: [{
        prop: 'date.from',
        operator: 'gte',
        value: moment().subtract(1, 'day')
      }],
      groupBy: ['date.hour'],
      title: 'Page Views in the past 24 hours',
      className: 'size-2of3'
    }),

    // --------------------------------------------------------------
    // Count: Number of Visitors Today
    // --------------------------------------------------------------
    widget(userCount, model, chan, conn, {
      filterBy: [{
        prop: 'date.from',
        operator: 'gte',
        value: moment().subtract(1, 'day')
      }],
      title: 'Number of Visitors Today'
    }),

    // --------------------------------------------------------------
    // Bar Chart: Visitors by Country
    // --------------------------------------------------------------
    widget(userBar, model, chan, conn, {
      groupBy: ['country'],
      title: 'Visitors by Country'
    }),

    // --------------------------------------------------------------
    // Count: Unique Visitors
    // --------------------------------------------------------------
    widget(userCount, model, chan, conn, {title: 'Unique Visitors'}),

    // --------------------------------------------------------------
    // Count: All Page Views
    // --------------------------------------------------------------
    widget(count, model, chan, conn, {
      collection: 'pageviews',
      title: 'All'
    }),

    // --------------------------------------------------------------
    // Pie Chart: Platform Statistics
    // --------------------------------------------------------------
    widget(pie, model, chan, conn, {
      collection: 'pageviews',
      filterBy: [{
        prop: 'date.from',
        operator: 'gte',
        value: moment().subtract(1, 'year')
      }],
      groupBy: ['date.year', 'user_agent.platform'],
      title: 'Platform Statistics'
    }),

    // --------------------------------------------------------------
    // Count Diff: Page View Difference Between Yesterday and Today
    // --------------------------------------------------------------
    widget(countDiff, model, chan, conn, {
      collection: 'pageviews',
      percentage: true,
      progressBar: true,
      from: {
        filterBy: [{
          prop: 'date.from',
          operator: 'gte',
          value: moment().subtract(1, 'day').startOf('day')
        }, {
          prop: 'date.to',
          operator: 'lte',
          value: moment().subtract(1, 'day').endOf('hour')
        }]
      },
      to: {
        filterBy: [{
          prop: 'date.from',
          operator: 'gte',
          value: moment().startOf('day')
        }, {
          prop: 'date.to',
          operator: 'lte',
          value: moment().endOf('hour')
        }]
      },
      title: `Difference between ${moment().subtract(1, 'days').endOf('hour').calendar()}
              and Today ${moment().endOf('hour').calendar()}`
    }),

    // --------------------------------------------------------------
    // Pie Chart: Mobile Operating Statistics
    // --------------------------------------------------------------
    widget(pie, model, chan, conn, {
      collection: 'pageviews',
      filterBy: [{
        prop: 'date.from',
        operator: 'gte',
        value: moment().subtract(1, 'year')
      }, {
        prop: 'user_agent.platform',
        operator: 'eq',
        value: 'mobile'
      }],
      groupBy: ['date.year', 'user_agent.os.family'],
      title: 'Mobile Operating Systems'
    }),

    // --------------------------------------------------------------
    // Sum: Total Revenue
    // --------------------------------------------------------------
    widget(sum, model, chan, conn, {
      collection: 'purchases',
      template: '$ {value}',
      prop: 'price',
      title: 'Total Revenue'
    }),

    // --------------------------------------------------------------
    // Recommendations
    // --------------------------------------------------------------
    widget(recommendations, model, chan),

    // --------------------------------------------------------------
    // Funnel
    // --------------------------------------------------------------
    widget(funnel, model, chan, conn, {
      name: 'items',
      title: 'Items 1-3 Funnel'
    })
  ]
}

export default makeWidgets
