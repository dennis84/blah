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
import mostViewedList from './most-viewed/list'
import referrers from './referrer/list'

/**
 * Pie Chart: Browser Statistics Over a Year
 */
function browserStats(model, update, conn) {
  return widget(pie, model, update, {}, conn, {
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
function pageviews(model, update, conn) {
  return widget(bar, model, update, {}, conn, {
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
function visitorsToday(model, update, conn) {
  return widget(userCount, model, update, {}, conn, {
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
function visitorsByCountry(model, update, conn) {
  return widget(userBar, model, update, {}, conn, {
    groupBy: ['country'],
    title: 'Visitors by Country'
  })
}

/**
 * Count: Unique Visitors
 */
function uniqueVisitors(model, update, conn) {
  return widget(userCount, model, update, {}, conn, {title: 'Unique Visitors'})
}

/**
 * Count one
 */
function countOne(model, update, conn, item) {
  return widget(count, model, update, {}, conn, {
    collection: 'pageviews',
    filterBy: [{
      prop: 'item',
      operator: 'eq',
      value: 'item-4'
    }],
    title: 'Count: ' + item
  })
}

/**
 * Count: All Page Views
 */
function countAll(model, update, conn) {
  return widget(count, model, update, {}, conn, {
    collection: 'pageviews',
    title: 'All'
  })
}

/**
 * Pie Chart: Platform Statistics
 */
function platformStats(model, update, conn) {
  return widget(pie, model, update, {}, conn, {
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
function pageviewDiff(model, update, conn) {
  return widget(countDiff, model, update, {}, conn, {
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
function mobileOsStats(model, update, conn) {
  return widget(pie, model, update, {}, conn, {
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
function totalRevenue(model, update, conn) {
  return widget(sum, model, update, {}, conn, {
    collection: 'purchases',
    template: '$ {value}',
    prop: 'price',
    title: 'Total Revenue'
  })
}

/**
 * Recommendations
 */
function recommendationsWidget(model, update, conn) {
  return widget(recommendations, model, update, {})
}

/**
 * Funnel
 */
function itemsFunnel(model, update, conn) {
  return widget(funnel, model, update, {}, conn, {
    name: 'items',
    title: 'Items 1-3 Funnel'
  })
}

/**
 * Most viewed items
 */
function mostViewed(model, update, conn) {
  return widget(mostViewedList, model, update, {}, conn, {
    title: 'Most Viewed Items',
    collection: 'pageviews',
    limit: 10
  })
}

/**
 * Referring Sites
 */
function referringSites(model, update, conn) {
  return widget(referrers, model, update, {}, conn, {
    title: 'Referring Sites',
    limit: 10
  })
}

export {
  browserStats,
  pageviews,
  visitorsToday,
  visitorsByCountry,
  uniqueVisitors,
  countAll,
  countOne,
  platformStats,
  pageviewDiff,
  mobileOsStats,
  totalRevenue,
  recommendationsWidget,
  itemsFunnel,
  mostViewed,
  referringSites
}
