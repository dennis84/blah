var clone = require('clone')
var http = require('../util/http')

/**
 * Fetch sum from serving layer.
 *
 * @param {Object} The widget state
 * @param {Object} Query options
 *
 * @return {Promise} The model wrapped in a promise
 */
function sum(model, options) {
  return http.post('/sum', mkQuery(options))
    .then(function(data) {
      var m = clone(model)
      m.sum = data.sum
      return m
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
