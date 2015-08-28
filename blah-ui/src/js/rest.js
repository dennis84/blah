import xhr from 'xhr'
import qs from './querystring'
import {SERVING_URL} from './config'

function url(base, path, params) {
  var str = SERVING_URL + path
  var queries = qs(params)
  if('' !== queries) {
    str += '?' + queries
  }

  return str
}

/**
 * Makes a GET call.
 *
 * @param {String} path   The api path
 * @param {Object} params Query params
 *
 * @return {Promise} A promise with parsed JSON data
 */
function get(path, params) {
  return new Promise((resolve, reject) => {
    xhr({
      uri: url(SERVING_URL, path, params),
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    }, function(err, resp, body) {
      resolve(JSON.parse(body))
    })
  })
}

export {
  get
}
