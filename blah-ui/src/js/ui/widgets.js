var moment = require('moment')
var component = require('./component')
var config = require('./../config')

/**
 * Pie Chart: Browser Statistics Over a Year
 */
function browserStats(update, ws) {
  return component(Count.Pie, {}, ws, {
    baseUrl: config.SERVING_URL,
    collection: 'view',
    filterBy: [{
      prop: 'date.from',
      operator: 'gte',
      value: moment().subtract(1, 'year')
    }],
    groupBy: ['date.year', 'user_agent.browser.family'],
    title: 'Browser Statistics Over a Year',
    update: update
  })
}

/**
 * Bar Chart: Page Views in the past 24 hours
 */
function pageviews(ws) {
  return component(Count.Bar, {}, ws, {
    baseUrl: config.SERVING_URL,
    collection: 'view',
    filterBy: [{
      prop: 'date.from',
      operator: 'gte',
      value: moment().subtract(1, 'day')
    }],
    groupBy: ['date.hour'],
    title: 'Page Views in the past 24 hours',
    class: {'size-2of3': true}
  })
}

/**
 * Count: Number of Visitors Today
 */
function visitorsToday(ws) {
  return component(User.Count, {}, ws, {
    baseUrl: config.SERVING_URL,
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
function visitorsByCountry(ws) {
  return component(User.Bar, {}, ws, {
    baseUrl: config.SERVING_URL,
    groupBy: ['country'],
    title: 'Visitors by Country'
  })
}

/**
 * Count: Unique Visitors
 */
function uniqueVisitors(ws) {
  return component(User.Count, {}, ws, {
    baseUrl: config.SERVING_URL,
    title: 'Unique Visitors'
  })
}

/**
 * Count one
 */
function countOne(ws, item) {
  return component(Count.Num, {}, ws, {
    baseUrl: config.SERVING_URL,
    collection: 'view',
    filterBy: [{
      prop: 'item',
      operator: 'eq',
      value: item
    }],
    title: 'Count: ' + item
  })
}

/**
 * Count: All Page Views
 */
function countAll(ws) {
  return component(Count.Num, {}, ws, {
    baseUrl: config.SERVING_URL,
    collection: 'view',
    title: 'All'
  })
}

/**
 * Pie Chart: Platform Statistics
 */
function platformStats(ws) {
  return component(Count.Pie, {}, ws, {
    baseUrl: config.SERVING_URL,
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
function pageviewDiff(ws) {
  var from = moment().subtract(1, 'days').endOf('hour').calendar()
  var to = moment().endOf('hour').calendar()
  var title = 'Difference between ' + from + ' and Today ' + to

  return component(Count.Diff, {}, ws, {
    baseUrl: config.SERVING_URL,
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
    title: title
  })
}

/**
 * Pie Chart: Mobile Operating Statistics
 */
function mobileOsStats(ws) {
  return component(Count.Pie, {}, ws, {
    baseUrl: config.SERVING_URL,
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
function totalRevenue(ws) {
  return component(Sum, {}, ws, {
    baseUrl: config.SERVING_URL,
    collection: 'buy',
    template: '$ {value}',
    prop: 'price',
    title: 'Total Revenue'
  })
}

/**
 * Recommendations
 */
function recommendationWidget(update) {
  return component(Recommendation, {}, {
    baseUrl: config.SERVING_URL,
    update: update
  })
}

/**
 * Simiarities
 */
function similarityWidget(update) {
  return component(Similarity, {}, {
    baseUrl: config.SERVING_URL,
    update: update
  })
}

/**
 * Most viewed items
 */
function mostViewed(ws) {
  return component(MostViewed, {}, ws, {
    baseUrl: config.SERVING_URL,
    title: 'Most Viewed Items',
    collection: 'view',
    limit: 10
  })
}

/**
 * Referring Sites
 */
function referringSites(ws) {
  return component(Referrer, {}, ws, {
    baseUrl: config.SERVING_URL,
    title: 'Referring Sites',
    limit: 10
  })
}

module.exports = {
  browserStats: browserStats,
  pageviews: pageviews,
  visitorsToday: visitorsToday,
  visitorsByCountry: visitorsByCountry,
  uniqueVisitors: uniqueVisitors,
  countAll: countAll,
  countOne: countOne,
  platformStats: platformStats,
  pageviewDiff: pageviewDiff,
  mobileOsStats: mobileOsStats,
  totalRevenue: totalRevenue,
  recommendationWidget: recommendationWidget,
  similarityWidget: similarityWidget,
  mostViewed: mostViewed,
  referringSites: referringSites
}
