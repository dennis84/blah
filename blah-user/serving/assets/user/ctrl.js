var clone = require('clone')
var xhr = require('xhr')

/**
 * Gets the user count from serving layer.
 *
 * `options.filterBy` Not implemented
 * `options.groupBy` Not implemented
 *
 * @param {Object} The widget state
 * @param {Object} Query options
 *
 * @return {Promise} The model wrapped in a promise
 */
function count(model, options) {
  return new Promise(function(resolve) {
    xhr.post(options.baseUrl + '/count', {
      json: mkQuery(options)
    }, function(err, resp, body) {
      var m = clone(model)
      m.count = body.count
      resolve(m)
    })
  })
}

/**
 * Fetch grouped users from serving layer.
 *
 * `options.filterBy` Not implemented
 * `options.groupBy` Group by properties e.g. `country`
 *
 * @param {Object} The widget state
 * @param {Object} Query options
 *
 * @return {Promise} The model wrapped in a promise
 */
function grouped(model, options) {
  return new Promise(function(resolve) {
    xhr.post(options.baseUrl + '/count', {
      json: mkQuery(options)
    }, function(err, resp, body) {
      var m = clone(model)
      m.users = body
      resolve(m)
    })
  })
}

function mkQuery(options) {
  var query = {}
  if(options.filterBy) {
    query.filterBy = options.filterBy
  }

  if(options.groupBy) {
    query.groupBy = options.groupBy
  }

  return query
}

module.exports = {
  count: count,
  grouped: grouped
}
