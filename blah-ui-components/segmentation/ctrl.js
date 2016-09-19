var clone = require('clone')
var xhr = require('xhr')

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
  return new Promise(function(resolve, reject) {
    xhr.post(options.baseUrl + '/count', {
      json: mkQuery(options)
    }, function(err, resp, body) {
      var m = clone(model)
      m.groups = body
      resolve(m)
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
  grouped: grouped
}
