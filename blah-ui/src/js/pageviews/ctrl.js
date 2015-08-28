import clone from 'clone'
import {get} from '../rest'

/**
 * Fetch pageviews from serving layer.
 *
 * `options.event` Filter by a specific event e.g. homepage
 * `options.from`  Filter by date
 * `options.to`    Filter by date
 *
 * @param {Object} The widget state
 * @param {Object} Query options
 *
 * @return {Promise} The model wrapped in a promise
 */
function count(model, options) {
  return get('/count', options).then((data) => {
    var m = clone(model)
    m.count = data.count
    return m
  })
}

/**
 * Fetch all pageviews from serving layer.
 *
 * `options.from`  Filter by date
 * `options.to`    Filter by date
 *
 * @param {Object} The widget state
 * @param {Object} Query options
 *
 * @return {Promise} The model wrapped in a promise
 */
function countAll(model, options) {
  return get('/count-all', options).then((data) => {
    var m = clone(model)
    m.views = data.views
    return m
  })
}

function incr(model) {
  return new Promise((resolve, reject) => {
    var m = clone(model)
    m.count ++
    resolve(m)
  })
}

export {
  count,
  countAll,
  incr
}
