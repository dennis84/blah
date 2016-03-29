/**
 * Sets the widget state into global state.
 *
 * @param {Object} The global state
 * @param {String} The widget ID
 * @param {Object} The widget state
 *
 * @return {Object} The global state
 */
function widget(model, id, m) {
  model[id] = m
  return model
}

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

export {
  widget,
  error,
  path
}
