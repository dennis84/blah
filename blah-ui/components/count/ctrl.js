var clone = require('clone')
var xhr = require('xhr')

/**
 * Fetch views from serving layer.
 *
 * `options.filterBy` An array of filters.
 * `options.groupBy` Group by properties e.g. `user_agent.browser.family`
 *
 * @param {Object} The widget state
 * @param {Object} Query options
 *
 * @return {Promise} The model wrapped in a promise
 */
function count(model, options) {
  return post(options.baseUrl + '/count', mkQuery(options))
    .then(function(data) {
      var m = clone(model)
      m.count = data.count
      return m
    })
}

/**
 * Sends two count requests and returns a diff.
 *
 * `options.from`
 * `options.to`
 *
 * @param {Object} The widget state
 * @param {Object} Query options
 *
 * @return {Promise} The model wrapped in a promise
 */
function countDiff(model, options) {
  var fromQuery = mkQuery(options.from)
  var toQuery = mkQuery(options.to)
  fromQuery.collection = options.collection
  toQuery.collection = options.collection

  var from = post(options.baseUrl + '/count', fromQuery)
    .then(function(data) {
      return data.count
    })

  var to = post(options.baseUrl + '/count', toQuery)
    .then(function(data) {
      return data.count
    })

  return Promise.all([from, to]).then(function(values) {
    var a = values[0]
    var b = values[1]
    var m = clone(model)
    m.from = a
    m.to = b
    if(true === options.percentage) {
      m.diff = b / a * 100
    } else {
      m.diff = a - b
    }

    return m
  })
}

/**
 * Fetch grouped views from serving layer.
 *
 * `options.filterBy` An array of filters.
 * `options.groupBy` An array of groups.
 *
 * @param {Object} The widget state
 * @param {Object} Query options
 *
 * @return {Promise} The model wrapped in a promise
 */
function grouped(model, options) {
  return post(options.baseUrl + '/count', mkQuery(options))
    .then(function(data) {
      var m = clone(model)
      m.groups = data
      return m
    })
}

function post(url, params) {
  return new Promise(function(resolve, reject) {
    xhr.post(url, {
      json: params
    }, function(err, resp, body) {
      if(err) reject(err)
      resolve(body)
    })
  })
}

function mkQuery(options) {
  var query = {}
  if(options.filterBy) {
    query.filterBy = options.filterBy
  }

  if(options.groupBy && options.groupBy.length > 0) {
    query.groupBy = options.groupBy
  }

  if(options.collection) {
    query.collection = options.collection
  }

  return query
}

module.exports = {
  count: count,
  countDiff: countDiff,
  grouped: grouped
}
