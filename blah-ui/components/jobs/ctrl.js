var clone = require('clone')
var http = require('../util/http')

/**
 * List jobs.
 *
 * @param {Object} The widget state
 * @param {Object} Query options
 *
 * @return {Promise} The model wrapped in a promise
 */
function list(model, options) {
  if(undefined === options) options = {}
  return http.get('/jobs', options)
    .then(function(data) {
      var m = clone(model)
      m.jobs = data
      return m
    })
}

/**
 * Run a job.
 *
 * @param {Object} The widget state
 * @param {String} The job name
 *
 * @return {Promise} The model wrapped in a promise
 */
function run(model, name) {
  return http.put('/jobs/' + name)
    .then(function(data) {
      var m = clone(model)
      var index = findIndexByName(m.jobs, name)
      m.jobs[index].clicked = true
      return m
    })
}

/**
 * Stop a job.
 *
 * @param {Object} The widget state
 * @param {String} The job name
 *
 * @return {Promise} The model wrapped in a promise
 */
function stop(model, name) {
  return del('/jobs/' + name)
    .then(function(data) {
      var m = clone(model)
      var index = findIndexByName(m.jobs, name)
      m.jobs[index].clicked = true
      return m
    })
}

function findIndexByName(xs, name) {
  for(var i in xs) {
    if(xs[i].name === name) return i
  }

  return -1
}

module.exports = {
  list: list,
  run: run,
  stop: stop
}
