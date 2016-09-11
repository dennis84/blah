import moment from 'moment'
import component from './common/component'
import {SERVING_URL} from './../config'

/**
 * Pie Chart: Browser Statistics Over a Year
 */
function browserStats(model, update, conn) {
  return component(Count.Pie, {}, conn.ws, {
    baseUrl: SERVING_URL,
    collection: 'view',
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
  return component(Count.Bar, {}, conn.ws, {
    baseUrl: SERVING_URL,
    collection: 'view',
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
function visitorsToday(conn) {
  return component(User.Count, {}, conn.ws, {
    baseUrl: SERVING_URL,
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
function visitorsByCountry(conn) {
  return component(User.Bar, {}, conn.ws, {
    baseUrl: SERVING_URL,
    groupBy: ['country'],
    title: 'Visitors by Country'
  })
}

/**
 * Count: Unique Visitors
 */
function uniqueVisitors(conn) {
  return component(User.Count, {}, conn.ws, {
    baseUrl: SERVING_URL,
    title: 'Unique Visitors'
  })
}

/**
 * Count one
 */
function countOne(model, update, conn, item) {
  return component(Count.Num, {}, conn.ws, {
    baseUrl: SERVING_URL,
    collection: 'view',
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
  return component(Count.Num, {}, conn.ws, {
    baseUrl: SERVING_URL,
    collection: 'view',
    title: 'All'
  })
}

/**
 * Pie Chart: Platform Statistics
 */
function platformStats(model, update, conn) {
  return component(Count.Pie, {}, conn.ws, {
    baseUrl: SERVING_URL,
    collection: 'view',
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
  return component(Count.Diff, {}, conn.ws, {
    baseUrl: SERVING_URL,
    collection: 'view',
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
  return component(Count.Pie, {}, conn.ws, {
    baseUrl: SERVING_URL,
    collection: 'view',
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
function totalRevenue(conn) {
  return component(Sum, {}, conn.ws, {
    baseUrl: SERVING_URL,
    collection: 'buy',
    template: '$ {value}',
    prop: 'price',
    title: 'Total Revenue'
  })
}

/**
 * Recommendations
 */
function recommendationWidget() {
  return component(Recommendation, {}, {
    baseUrl: SERVING_URL
  })
}

/**
 * Simiarities
 */
function similarityWidget() {
  return component(Similarity, {}, {
    baseUrl: SERVING_URL
  })
}

/**
 * Most viewed items
 */
function mostViewed(conn) {
  return component(MostViewed, {}, conn.ws, {
    baseUrl: SERVING_URL,
    title: 'Most Viewed Items',
    collection: 'view',
    limit: 10
  })
}

/**
 * Referring Sites
 */
function referringSites(conn) {
  return component(Referrer, {}, conn.ws, {
    baseUrl: SERVING_URL,
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
  recommendationWidget,
  similarityWidget,
  mostViewed,
  referringSites
}
