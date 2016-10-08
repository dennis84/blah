var h = require('snabbdom/h')

function render(model, options) {
  return h('div', model.apps.map(function(app) {
    return h('div', [
      h('h2.title.is-2', app.app),
      h('div', 'Instances: ' + app.instances + '/' + app.max_instances),
      h('progress', {
        class: {'progress': true, 'is-primary': true},
        props: {value: app.instances, max: app.max_instances}
      }),
      h('div', 'CPU Usage: ' + app.cpu_usage),
      h('progress', {
        class: {'progress': true, 'is-primary': true},
        props: {value: app.cpu_usage, max: 100}
      }),
      h('div', 'Memory Usage: ' + app.mem_usage),
      h('progress', {
        class: {'progress': true, 'is-primary': true},
        props: {value: app.mem_usage, max: 100}
      })
    ])
  }))
}

module.exports = render
