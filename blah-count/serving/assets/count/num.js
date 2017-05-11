var h = require('snabbdom/h').default

function content(model, options) {
  if(undefined === model.count) return []
  return [h('div.has-text-centered', [
    h('div.value', String(model.count)),
    h('div.title', options.title)
  ])]
}

function render(model, update, options) {
  return h('div.count-num.box.widget', content(model, options))
}

module.exports = render
