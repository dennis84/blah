var h = require('snabbdom/h')

function content(model, options) {
  if(undefined === model.sum) return []
  var value = options.template ?
    options.template.replace('{value}', model.sum.toFixed(2)) :
    String(model.sum.toFixed(2))

  return [h('div', [
    h('div.widget-value', value),
    h('div.widget-title', options.title)
  ])]
}

function render(model, update, options) {
  return h('div.widget.widget-sum.is-centered-hv', {
    class: options.class
  }, content(model, options))
}

module.exports = render
