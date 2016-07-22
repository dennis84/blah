var xhr = require('xhr')
var API_URL = 'http://api.blah.local'

/**
 * Tracking API.
 *
 * ```
 * track('view', {
 *   'item': 'home',
 *   'title': 'Visited page',
 *   'user': 'username'
 * })
 * ```
 *
 * @param {String} collection
 * @param {Object} params
 *
 * @return {Promise}
 */
function track(collection, params) {
  return new Promise(function(resolve, reject) {
    xhr({
      uri: API_URL + '/events/' + collection,
      method: 'POST',
      json: params,
      timeout: 10000,
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    }, function(err, resp, body) {
      if(200 !== resp.statusCode) reject(resp)
      resolve(body)
    })
  })
}

module.exports = track
