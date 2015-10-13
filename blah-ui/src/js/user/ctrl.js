import clone from 'clone'
import {get} from '../rest'

/**
 * Gets the user count from serving layer.
 *
 * @return {Promise} The model wrapped in a promise
 */
function count(model) {
  return get('/user').then((data) => {
    var m = clone(model)
    m.count = data.count
    return m
  })
}

export {
  count
}
