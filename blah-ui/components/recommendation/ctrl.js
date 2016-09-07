var clone = require('clone')
var http = require('../util/http')

/**
 * Fetch user recommendations from serving layer.
 *
 * `options.user` The username.
 *
 * @param {Object} The widget state
 * @param {Object} Query options
 *
 * @return {Promise} The model wrapped in a promise
 */
function find(model, options) {
  return http.post('/recommendation', options)
    .then(function(data) {
      var m = clone(model)
      m.user = options.user
      m.items = data
      return m
    })
}

module.exports = {
  find: find
}
