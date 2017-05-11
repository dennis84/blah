var h = require('snabbdom/h').default

function render(model, update, options) {
  return h('div.user-count.box.widget', {
    class: options.class
  }, h('div.has-text-centered', [
    h('div.value', String(model.count ? model.count : 0)),
    h('div.title', options.title)
  ]))
}

module.exports = render
