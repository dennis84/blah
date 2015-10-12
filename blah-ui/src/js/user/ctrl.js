import clone from 'clone'
import {get} from '../rest'

/**
 * Fetch all users from serving layer.
 *
 * @return {Promise} The model wrapped in a promise
 */
function count(model) {
  return get('/user').then((data) => {
    var m = clone(model)
    m.users = data
    return m
  })
}

export {
  count
}
