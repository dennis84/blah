var subYears = require('date-fns/sub_years')
var subDays = require('date-fns/sub_days')
var endOfHour = require('date-fns/end_of_hour')
var startOfDay = require('date-fns/start_of_day')
var component = require('./component')

/**
 * Pie Chart: Browser Statistics Over a Year
 */
function browserStats(update, events) {
  return component(window.Count.Pie, {}, events, {
    collection: 'view',
    filterBy: [{
      prop: 'date.from',
      operator: 'gte',
      value: subYears(Date.now(), 1).toISOString()
    }],
    groupBy: ['date.year', 'user_agent.browser.family'],
    title: 'Browser Statistics Over a Year',
    update: update
  })
}

/**
 * Bar Chart: Page Views in the last 24 hours
 */
function pageviews(events) {
  return component(window.Count.Bar, {}, events, {
    collection: 'view',
    filterBy: [{
      prop: 'date.from',
      operator: 'gte',
      value: subDays(Date.now(), 1).toISOString()
    }],
    groupBy: ['date.hour'],
    title: 'Page Views in the last 24 hours',
    class: {'is-2': true}
  })
}

/**
 * Count: Number of Visitors Today
 */
function visitorsToday(events) {
  return component(window.User.Count, {}, events, {
    filterBy: [{
      prop: 'date.from',
      operator: 'gte',
      value: subDays(Date.now(), 1).toISOString()
    }],
    title: 'Number of Visitors Today'
  })
}

/**
 * Bar Chart: Visitors by Country
 */
function visitorsByCountry(events) {
  return component(window.User.Bar, {}, events, {
    groupBy: ['country'],
    title: 'Visitors by Country'
  })
}

/**
 * Count: Unique Visitors
 */
function uniqueVisitors(events) {
  return component(window.User.Count, {}, events, {
    title: 'Unique Visitors'
  })
}

/**
 * Count one
 */
function countOne(events, item) {
  return component(window.Count.Num, {}, events, {
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
function countAll(events) {
  return component(window.Count.Num, {}, events, {
    collection: 'view',
    title: 'All'
  })
}

/**
 * Pie Chart: Platform Statistics
 */
function platformStats(events) {
  return component(window.Count.Pie, {}, events, {
    collection: 'view',
    filterBy: [{
      prop: 'date.from',
      operator: 'gte',
      value: subYears(Date.now(), 1).toISOString()
    }],
    groupBy: ['date.year', 'user_agent.platform'],
    title: 'Platform Statistics'
  })
}

/**
 * Pie Chart: Mobile Operating Statistics
 */
function mobileOsStats(events) {
  return component(window.Count.Pie, {}, events, {
    collection: 'view',
    filterBy: [{
      prop: 'date.from',
      operator: 'gte',
      value: subYears(Date.now(), 1).toISOString()
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
function totalRevenue(events) {
  return component(window.Sum, {}, events, {
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
  return component(window.Recommendation, {}, {
    update: update
  })
}

/**
 * Simiarities
 */
function similarityWidget(update) {
  return component(window.Similarity, {}, {
    update: update
  })
}

/**
 * Most viewed items
 */
function mostViewed(events) {
  return component(window.MostViewed, {}, events, {
    title: 'Most Viewed Items',
    collection: 'view',
    limit: 10
  })
}

/**
 * Referring Sites
 */
function referringSites(events) {
  return component(window.Referrer, {}, events, {
    limit: 10
  })
}

/**
 * Collection Count
 */
function collectionCount(events) {
  return component(window.CollectionCount, {}, events, {
    collection: 'view',
    title: 'Number of "view" Events',
    class: {'is-2': true}
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
  mobileOsStats: mobileOsStats,
  totalRevenue: totalRevenue,
  recommendationWidget: recommendationWidget,
  similarityWidget: similarityWidget,
  mostViewed: mostViewed,
  referringSites: referringSites,
  collectionCount: collectionCount
}
