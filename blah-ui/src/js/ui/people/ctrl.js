import clone from 'clone'
import {post} from '../../http'

/**
 * Search users.
 *
 * @param {Object} The widget state
 * @param {Object} Query options
 *
 * @return {Promise} The model wrapped in a promise
 */
function search(model, options) {
  return post('/users', options).then((data) => {
    var m = clone(model)
    m.users = data
    return m
  })
}

export {
  search
}
