var h = require('snabbdom/h')
var chart = require('./chart')

function renderChart(app, vnode) {
  if(!app.empty) setTimeout(function() {
    chart(vnode.elm, app)
  }, 0)
}

function render(model, options) {
  return h('div.autoscale.columns.is-multiline', model.apps.map(function(app) {
    return h('div.column.is-2', [
      h('div.app', {
        class: {'is-empty': app.empty}
      }, [
        h('div.chart', {
          hook: {
            insert: renderChart.bind(null, app),
            update: renderChart.bind(null, app),
          }
        }),
        h('div.content.is-centered-hv', [h('div', [
          h('div.name', app.app),
          !app.empty ? h('div.cpu-usage', 'CPU: ' + app.cpu_usage + '%') : '',
          !app.empty ? h('div.mem-usage', 'RAM: ' + app.mem_usage + '%') : ''
        ])])
      ])
    ])
  }))
}

module.exports = render
