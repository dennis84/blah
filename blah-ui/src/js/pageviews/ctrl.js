import clone from 'clone'
import {get,post} from '../rest'

/**
 * Fetch pageviews from serving layer.
 *
 * `options.filterBy` An object of filters.
 *
 * @param {Object} The widget state
 * @param {Object} Query options
 *
 * @return {Promise} The model wrapped in a promise
 */
function count(model, options) {
  return post('/count', options).then((data) => {
    var m = clone(model)
    m.count = data.count
    return m
  })
}

/**
 * Fetch grouped pageviews from serving layer.
 *
 * `options.filterBy` Filter by a specific properties
 * `options.groupBy` Group by a specific properties
 *
 * @param {Object} The widget state
 * @param {Object} Query options
 *
 * @return {Promise} The model wrapped in a promise
 */
function grouped(model, options) {
  return post('/count', options).then((data) => {
    var m = clone(model)
    m.groups = data
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
  grouped,
  incr
}
