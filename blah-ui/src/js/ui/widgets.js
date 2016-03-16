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

/**
 * Pie Chart: Browser Statistics Over a Year
 */
function browserStats(model, chan, conn) {
  return widget(pie, model, chan, {}, conn, {
    collection: 'pageviews',
    filterBy: [{
      prop: 'date.from',
      operator: 'gte',
      value: moment().subtract(1, 'year')
    }],
    groupBy: ['date.year', 'user_agent.browser.family'],
    title: 'Browser Statistics Over a Year'
  })
}

/**
 * Bar Chart: Page Views in the past 24 hours
 */
function pageviews(model, chan, conn) {
  return widget(bar, model, chan, {}, conn, {
    collection: 'pageviews',
    filterBy: [{
      prop: 'date.from',
      operator: 'gte',
      value: moment().subtract(1, 'day')
    }],
    groupBy: ['date.hour'],
    title: 'Page Views in the past 24 hours',
    className: 'size-2of3'
  })
}

/**
 * Count: Number of Visitors Today
 */
function visitorsToday(model, chan, conn) {
  return widget(userCount, model, chan, {}, conn, {
    filterBy: [{
      prop: 'date.from',
      operator: 'gte',
      value: moment().subtract(1, 'day')
    }],
    title: 'Number of Visitors Today'
  })
}

/**
 * Bar Chart: Visitors by Country
 */
function visitorsByCountry(model, chan, conn) {
  return widget(userBar, model, chan, {}, conn, {
    groupBy: ['country'],
    title: 'Visitors by Country'
  })
}

/**
 * Count: Unique Visitors
 */
function uniqueVisitors(model, chan, conn) {
  return widget(userCount, model, chan, {}, conn, {title: 'Unique Visitors'})
}

/**
 * Count: All Page Views
 */
function countAll(model, chan, conn) {
  return widget(count, model, chan, {}, conn, {
    collection: 'pageviews',
    title: 'All'
  })
}

/**
 * Pie Chart: Platform Statistics
 */
function platformStats(model, chan, conn) {
  return widget(pie, model, chan, {}, conn, {
    collection: 'pageviews',
    filterBy: [{
      prop: 'date.from',
      operator: 'gte',
      value: moment().subtract(1, 'year')
    }],
    groupBy: ['date.year', 'user_agent.platform'],
    title: 'Platform Statistics'
  })
}

/**
 * Count Diff: Page View Difference Between Yesterday and Today
 */
function pageviewDiff(model, chan, conn) {
  return widget(countDiff, model, chan, {}, conn, {
    collection: 'pageviews',
    percentage: true,
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
  })
}

/**
 * Pie Chart: Mobile Operating Statistics
 */
function mobileOsStats(model, chan, conn) {
  return widget(pie, model, chan, {}, conn, {
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
  })
}

/**
 * Sum: Total Revenue
 */
function totalRevenue(model, chan, conn) {
  return widget(sum, model, chan, {}, conn, {
    collection: 'purchases',
    template: '$ {value}',
    prop: 'price',
    title: 'Total Revenue'
  })
}

/**
 * Recommendations
 */
function recommendationsWidget(model, chan, conn) {
  return widget(recommendations, model, chan, {})
}

/**
 * Funnel
 */
function itemsFunnel(model, chan, conn) {
  return widget(funnel, model, chan, {}, conn, {
    name: 'items',
    title: 'Items 1-3 Funnel'
  })
}

export {
 browserStats,
 pageviews,
 visitorsToday,
 visitorsByCountry,
 uniqueVisitors,
 countAll,
 platformStats,
 pageviewDiff,
 mobileOsStats,
 totalRevenue,
 recommendationsWidget,
 itemsFunnel
}
