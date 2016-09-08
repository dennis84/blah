var clone = require('clone')
var xhr = require('xhr')

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
  return new Promise(function(resolve, reject) {
    xhr.post(options.baseUrl + '/most-viewed', {
      json: mkQuery(options)
    }, function(err, resp, body) {
      var m = clone(model)
      m.collection = options.collection
      m.items = body
      resolve(m)
    })
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
