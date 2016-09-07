var clone = require('clone')
var http = require('../util/http')

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
  return http.post('/similarity', options)
    .then(function(data) {
      var m = clone(model)
      m.items = options.items
      m.similarities = data
      return m
    })
}

module.exports = {
  find: find
}
