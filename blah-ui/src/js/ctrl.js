/**
 * Handle errors.
 *
 * @param {Object} model The global state
 * @param {Object} obj Error object
 *
 * @return {Object} The global state
 */
function error(model, obj) {
  model.error = 'unknown'
  return model
}

/**
 * Changes the URL path.
 *
 * @param {Object} The global state
 * @param {String} The new path
 *
 * @return {Object} The global state
 */
function path(model, path) {
  model.error = null
  model.path = path
  return model
}

/**
 * Toggle between light and dark theme.
 *
 * @param {Object} The global state
 *
 * @return {Object} The updated state
 */
function theme(model, storage) {
  model.theme = 'dark' === model.theme ? 'light' : 'dark'
  storage.set('settings', {theme: model.theme})
  return model
}

module.exports = {
  error: error,
  path: path,
  theme: theme
}
