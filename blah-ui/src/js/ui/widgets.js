var subYears = require('date-fns/sub_years')
var subDays = require('date-fns/sub_days')
var component = require('./component')
var ctrl = require('../ctrl')

function browserStats(events) {
  return component(window.Count.Pie, {}, events, {
    collection: 'view',
    filterBy: [{
      prop: 'date.from',
      operator: 'gte',
      value: subYears(Date.now(), 1).toISOString()
    }],
    groupBy: ['date.year', 'user_agent.browser.family'],
    title: 'Browser Statistics Over a Year',
  })
}

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

function visitorsByCountry(events) {
  return component(window.User.Bar, {}, events, {
    groupBy: ['country'],
    title: 'Visitors by Country'
  })
}

function uniqueVisitors(events) {
  return component(window.User.Count, {}, events, {
    title: 'Unique Visitors'
  })
}

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

function countAll(events) {
  return component(window.Count.Num, {}, events, {
    collection: 'view',
    title: 'All'
  })
}

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

function totalRevenue(events) {
  return component(window.Sum, {}, events, {
    collection: 'buy',
    template: '$ {value}',
    prop: 'price',
    title: 'Total Revenue'
  })
}

function recommendationWidget(update) {
  return component(window.Recommendation, {}, {
    on: {update: function() {
      update(ctrl.noop)
    }}
  })
}

function similarityWidget(update) {
  return component(window.Similarity, {}, {
    on: {update: function() {
      update(ctrl.noop)
    }}
  })
}

function mostViewed(events) {
  return component(window.MostViewed, {}, events, {
    title: 'Most Viewed Items',
    collection: 'view',
    limit: 10
  })
}

function referringSites(events) {
  return component(window.Referrer, {}, events, {
    limit: 10
  })
}

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
