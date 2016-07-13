import clone from 'clone'
import {post} from '../../http'

function openTab(model, tab) {
  var m = clone(model)
  m.activeTab = tab
  return m
}

/**
 * Search for Funnels.
 *
 * `options.name` The funnel name
 *
 * @param {Object} The widget state
 * @param {Object} Query options
 *
 * @return {Promise} The model wrapped in a promise
 */
function search(model, options) {
  return post('/funnel', mkQuery(options)).then((data) => {
    var m = clone(model)
    m.name = options.name
    m.items = data
    return m
  })
}

function mkQuery(options) {
  var query = {}

  if(options.name) {
    query.name = options.name
  }

  return query
}

export {
  openTab,
  search
}
