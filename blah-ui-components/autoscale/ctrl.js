var clone = require('clone')

/**
 * Update apps.
 */
function update(model, data) {
  var m = clone(model)
  m.apps = data
  return m
}

module.exports = {
  update: update
}
