var clone = require('clone')
var xhr = require('xhr')

/**
 * List jobs.
 *
 * @param {Object} The widget state
 * @param {Object} Query options
 *
 * @return {Promise} The model wrapped in a promise
 */
function list(model, options) {
  return new Promise(function(resolve) {
    xhr.get(options.baseUrl + '/jobs', {}, function(err, resp, body) {
      var m = clone(model)
      m.jobs = JSON.parse(body)
      resolve(m)
    })
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
function run(model, name, options) {
  return new Promise(function(resolve) {
    xhr.put(options.baseUrl + '/jobs/' + name, {}, function() {
      var m = clone(model)
      var index = findIndexByName(m.jobs, name)
      m.jobs[index].clicked = true
      resolve(m)
    })
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
function stop(model, name, options) {
  return new Promise(function(resolve) {
    xhr.del(options.baseUrl + '/jobs/' + name, {}, function() {
      var m = clone(model)
      var index = findIndexByName(m.jobs, name)
      m.jobs[index].clicked = true
      resolve(m)
    })
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
