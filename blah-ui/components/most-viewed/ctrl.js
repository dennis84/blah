var clone = require('clone')
var http = require('../util/http')

/**
 * Find most viewed items by collection.
 *
 * `options.collection` The collection name
 *
 * @param {Object} The widget state
 * @param {Object} Query options
 *
 * @return {Promise} The model wrapped in a promise
 */
function find(model, options) {
  return http.post('/most-viewed', mkQuery(options))
    .then(function(data) {
      var m = clone(model)
      m.collection = options.collection
      m.items = data
      return m
    })
}

function mkQuery(options) {
  var query = {}

  if(options.collection) {
    query.collection = options.collection
  }

  query.limit = options.limit || 100

  return query
}

module.exports = {
  find: find
}
