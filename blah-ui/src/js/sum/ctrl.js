import clone from 'clone'
import {post} from '../rest'

/**
 * Fetch sum from serving layer.
 *
 * @param {Object} The widget state
 * @param {Object} Query options
 *
 * @return {Promise} The model wrapped in a promise
 */
function sum(model, options) {
  return post('/sum', mkQuery(options)).then((data) => {
    var m = clone(model)
    m.sum = data.sum
    return m
  })
}

function mkQuery(options) {
  var query = {}
  if(options.filterBy) {
    query.filterBy = options.filterBy
  }

  if(options.prop) {
    query.prop = options.prop
  }

  if(options.collection) {
    query.collection = options.collection
  }

  return query
}

export {
  sum
}
