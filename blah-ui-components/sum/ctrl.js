var clone = require('clone')
var xhr = require('xhr')

/**
 * Fetch sum from serving layer.
 *
 * @param {Object} The widget state
 * @param {Object} Query options
 *
 * @return {Promise} The model wrapped in a promise
 */
function sum(model, options) {
  return new Promise(function(resolve, reject) {
    xhr.post(options.baseUrl + '/sum', {
      json: mkQuery(options)
    }, function(err, resp, body) {
      if(err) {
        reject(err)
        return
      }

      var m = clone(model)
      m.sum = body.sum
      resolve(m)
    })
  })
}

function mkQuery(options) {
  var query = {}
  if(options.filterBy) {
    query.filterBy = options.filterBy
  }

  if(options.prop) {
    query.prop = options.prop
  }

  if(options.collection) {
    query.collection = options.collection
  }

  return query
}

module.exports = {
  sum: sum
}
