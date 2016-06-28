import clone from 'clone'
import {post} from '../../http'

/**
 * Find all referrers.
 *
 * `options.limit` The limit
 *
 * @param {Object} The widget state
 * @param {Object} Query options
 *
 * @return {Promise} The model wrapped in a promise
 */
function search(model, options) {
  return post('/referrer', mkQuery(options)).then((data) => {
    var m = clone(model)
    m.referrers = data
    return m
  })
}

function mkQuery(options) {
  var query = {}

  query.limit = options.limit || 100

  return query
}

export {
  search
}
