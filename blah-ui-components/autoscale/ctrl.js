var clone = require('clone')

/**
 * Update apps.
 */
function update(model, data) {
  var m = clone(model)
  var apps = []
  for(var i in data) {
    var app = data[i]
    for(var j = 0; j < app.max_instances; j ++) {
      apps.push({
        app: app.app,
        instances: app.instances,
        max_instances: app.max_instances,
        cpu_usage: app.cpu_usage,
        mem_usage: app.mem_usage,
        empty: j >= app.instances
      })
    }
  }

  m.apps = apps
  return m
}

module.exports = {
  update: update
}
