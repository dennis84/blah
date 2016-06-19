import clone from 'clone'
import {post} from '../../http'

/**
 * Find most viewed items by collection.
 *
 * `options.collection` The collection name
 *
 * @param {Object} The widget state
 * @param {Object} Query options
 *
 * @return {Promise} The model wrapped in a promise
 */
function find(model, options) {
  return post('/most-viewed', mkQuery(options)).then((data) => {
    var m = clone(model)
    m.collection = data.collection
    m.items = data.items
    return m
  })
}

function mkQuery(options) {
  var query = {}

  if(options.collection) {
    query.collection = options.collection
  }

  return query
}

export {
  find
}
