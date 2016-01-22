import clone from 'clone'
import {get,post} from '../rest'

/**
 * Fetch views from serving layer.
 *
 * `options.filterBy` An object of filters.
 *
 * @param {Object} The widget state
 * @param {Object} Query options
 *
 * @return {Promise} The model wrapped in a promise
 */
function count(model, options) {
  return post('/count', mkQuery(options)).then((data) => {
    var m = clone(model)
    m.count = data.count
    return m
  })
}

/**
 * Fetch grouped views from serving layer.
 *
 * `options.filterBy` Filter by specific properties
 * `options.groupBy` Group by specific properties
 *
 * @param {Object} The widget state
 * @param {Object} Query options
 *
 * @return {Promise} The model wrapped in a promise
 */
function grouped(model, options) {
  return post('/count', mkQuery(options)).then((data) => {
    var m = clone(model)
    m.groups = data
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

  if(options.collection) {
    query.collection = options.collection
  }

  return query
}

export {
  count,
  grouped
}
