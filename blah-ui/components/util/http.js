var xhr = require('xhr')
var qs = require('./querystring')
var SERVING_URL = 'http://serving.blah.local'

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
  return new Promise(function(resolve, reject) {
    xhr({
      uri: url(path, params),
      method: 'GET',
      timeout: 10000,
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    }, function(err, resp, body) {
      resolve(JSON.parse(body))
    })
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
  if(undefined === params) params = {}
  return new Promise(function(resolve, reject) {
    xhr({
      uri: url(path),
      method: 'POST',
      json: params,
      timeout: 10000,
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    }, function(err, resp, body) {
      if(0 === resp.statusCode) reject(resp)
      resolve(body)
    })
  })
}

function put(path, params) {
  return new Promise(function(resolve, reject) {
    xhr({
      uri: url(path),
      method: 'PUT',
      json: params,
      timeout: 10000,
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    }, function(err, resp, body) {
      if(0 === resp.statusCode) reject(resp)
      resolve(body)
    })
  })
}

/**
 * Makes a DELETE call.
 *
 * @param {String} path   The api path
 * @param {Object} params Query params
 *
 * @return {Promise} A promise with parsed JSON data
 */
function del(path, params) {
  return new Promise(function(resolve, reject) {
    xhr({
      uri: url(path, params),
      method: 'DELETE',
      timeout: 10000,
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    }, function(err, resp, body) {
      resolve(JSON.parse(body))
    })
  })
}

module.exports = {
  get: get,
  post: post,
  put: put,
  del: del
}
