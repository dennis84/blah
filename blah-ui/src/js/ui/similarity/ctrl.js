import clone from 'clone'
import {post} from '../../http'

/**
 * Fetch similar items from serving layer.
 *
 * `options.item` The item.
 *
 * @param {Object} The widget state
 * @param {Object} Query options
 *
 * @return {Promise} The model wrapped in a promise
 */
function find(model, options) {
  return post('/similarity', options).then((data) => {
    var m = clone(model)
    m.items = options.items
    m.similarities = data
    return m
  })
}

export {
  find
}
