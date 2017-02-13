var clone = require('clone')
var xhr = require('xhr')

function openTab(model, tab) {
  var m = clone(model)
  m.activeTab = tab
  return m
}

/**
 * Search for Funnels.
 *
 * `options.name` The funnel name
 *
 * @param {Object} The widget state
 * @param {Object} Query options
 *
 * @return {Promise} The model wrapped in a promise
 */
function search(model, options) {
  return new Promise(function(resolve) {
    xhr.post(options.baseUrl + '/funnels', {
      json: mkQuery(options)
    }, function(err, resp, body) {
      var m = clone(model)
      m.name = options.name
      m.items = body
      resolve(m)
    })
  })
}

function mkQuery(options) {
  var query = {}

  if(options.name) {
    query.name = options.name
  }

  return query
}

module.exports = {
  openTab: openTab,
  search: search
}
