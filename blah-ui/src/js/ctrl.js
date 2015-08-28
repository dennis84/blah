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

function incr(model) {
  model.count += 1
  return model
}

export {
  widget,
  incr
}
