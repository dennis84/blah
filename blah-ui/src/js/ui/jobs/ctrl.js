import clone from 'clone'
import {get, put, del} from '../../http'

/**
 * List jobs.
 *
 * @param {Object} The widget state
 * @param {Object} Query options
 *
 * @return {Promise} The model wrapped in a promise
 */
function list(model, options = {}) {
  return get('/jobs', options).then(data => {
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
  return put('/jobs/' + name).then(data => {
    var m = clone(model)
    var index = findIndexByName(m.jobs, name)
    m.jobs[index].running = true
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
  return del('/jobs/' + name).then(data => {
    var m = clone(model)
    var index = findIndexByName(m.jobs, name)
    m.jobs[index].running = false
    return m
  })
}

function findIndexByName(xs, name) {
  for(var i in xs) {
    if(xs[i].name === name) return i
  }

  return -1
}

export {
  list,
  run,
  stop
}
