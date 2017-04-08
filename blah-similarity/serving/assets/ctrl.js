var clone = require('clone')
var xhr = require('xhr')

/**
 * Fetch similar items from serving layer.
 *
 * `options.item` The item.
 *
 * @param {Object} The widget state
 * @param {Object} Query options
 *
 * @return {Promise} The model wrapped in a promise
 */
function find(model, options) {
  return new Promise(function(resolve) {
    xhr.post(options.baseUrl + '/similarities', {
      json: mkQuery(options)
    }, function(err, resp, body) {
      var m = clone(model)
      m.items = options.items
      m.similarities = body
      resolve(m)
    })
  })
}

function mkQuery(options) {
  var query = {}
  query.items = options.items || []
  if(options.collection) query.collection = options.collection
  if(options.limit) query.limit = options.limit
  return query
}

module.exports = {
  find: find
}
