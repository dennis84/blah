var clone = require('clone')

/**
 * Update apps.
 */
function update(model, data) {
  var m = clone(model)
  var apps = []
  var prev = null
  for(var i in data) {
    var app = data[i]

    if(prev && prev.app !== app.app) {
      for(var j = prev.instances; j < prev.max_instances; j ++) {
        apps.push({
          app: prev.app,
          instances: prev.instances,
          max_instances: prev.max_instances,
          empty: true
        })
      }
    }

    apps.push(app)
    prev = app
  }

  m.apps = apps
  return m
}

module.exports = {
  update: update
}
