import clone from 'clone'
import {post} from '../../http'

/**
 * Gets the user count from serving layer.
 *
 * `options.filterBy` Not implemented
 * `options.groupBy` Not implemented
 *
 * @param {Object} The widget state
 * @param {Object} Query options
 *
 * @return {Promise} The model wrapped in a promise
 */
function count(model, options) {
  return post('/user-count', mkQuery(options)).then((data) => {
    var m = clone(model)
    m.count = data.count
    return m
  })
}

/**
 * Fetch grouped users from serving layer.
 *
 * `options.filterBy` Not implemented
 * `options.groupBy` Group by properties e.g. `country`
 *
 * @param {Object} The widget state
 * @param {Object} Query options
 *
 * @return {Promise} The model wrapped in a promise
 */
function grouped(model, options) {
  return post('/user-count', mkQuery(options)).then((data) => {
    var m = clone(model)
    m.users = data
    return m
  })
}

function mkQuery(options) {
  var query = {}
  if(options.filterBy) {
    query.filterBy = options.filterBy
  }

  if(options.groupBy) {
    query.groupBy = options.groupBy
  }

  return query
}

export {
  count,
  grouped
}
