var h = require('snabbdom/h').default

function content(model, options) {
  if(undefined === model.count) return []
  return [h('div', [
    h('div.widget-value', String(model.count)),
    h('div.widget-title', options.title)
  ])]
}

function render(model, update, options) {
  return h('div.widget.widget-users.is-centered-hv', {
    class: options.class
  }, content(model, options))
}

module.exports = render
