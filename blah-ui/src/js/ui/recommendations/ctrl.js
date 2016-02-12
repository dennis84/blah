import clone from 'clone'
import {post} from '../../http'

/**
 * Fetch user recommendations from serving layer.
 *
 * `options.user` The username.
 *
 * @param {Object} The widget state
 * @param {Object} Query options
 *
 * @return {Promise} The model wrapped in a promise
 */
function recommendations(model, options) {
  return post('/similarity', options).then((data) => {
    var m = clone(model)
    m.user = data.user
    m.views = data.views
    return m
  })
}

export {
  recommendations
}
