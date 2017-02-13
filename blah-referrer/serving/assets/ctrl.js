var clone = require('clone')
var xhr = require('xhr')

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
  return new Promise(function(resolve) {
    xhr.post(options.baseUrl + '/referrers', {
      json: mkQuery(options)
    }, function(err, resp, body) {
      var m = clone(model)
      m.referrers = body
      resolve(m)
    })
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
