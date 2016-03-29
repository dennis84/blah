import xhr from 'xhr'
import qs from './querystring'
import {SERVING_URL} from './config'

function url(path, params) {
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
      uri: url(path, params),
      method: 'GET',
      timeout: 10000,
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    }, (err, resp, body) => resolve(JSON.parse(body)))
  })
}

/**
 * Makes a POST call.
 *
 * @param {String} path   The api path
 * @param {Object} params Query params
 *
 * @return {Promise} A promise with parsed JSON data
 */
function post(path, params) {
  return new Promise((resolve, reject) => {
    xhr({
      uri: url(path),
      method: 'POST',
      json: params,
      timeout: 10000,
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    }, (err, resp, body) => {
      if(0 === resp.statusCode) reject(resp)
      resolve(body)
    })
  })
}

export {
  get,
  post
}
