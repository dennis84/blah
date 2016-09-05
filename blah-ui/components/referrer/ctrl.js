var clone = require('clone')
var http = require('../util/http')

/**
 * Find all referrers.
 *
 * `options.limit` The limit
 *
 * @param {Object} The widget state
 * @param {Object} Query options
 *
 * @return {Promise} The model wrapped in a promise
 */
function search(model, options) {
  return http.post('/referrer', mkQuery(options))
    .then(function(data) {
      var m = clone(model)
      m.referrers = data
      return m
    })
}

function mkQuery(options) {
  var query = {}

  query.limit = options.limit || 100

  return query
}

module.exports = {
  search: search
}
