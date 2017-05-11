var h = require('snabbdom/h').default

function content(model, options) {
  if(undefined === model.sum) return []
  var value = options.template ?
    options.template.replace('{value}', model.sum.toFixed(2)) :
    String(model.sum.toFixed(2))

  return [h('div', [
    h('div.value', value),
    h('div.title', options.title)
  ])]
}

function render(model, update, options) {
  return h('div.count-sum.box.widget', {
    class: options.class
  }, content(model, options))
}

module.exports = render
