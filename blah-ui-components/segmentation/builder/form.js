var h = require('snabbdom/h')

function render(model, update) {
  var views = [].slice.call(arguments, 2)
  return h('form', views.map(function(x) {
    return x(model, update)
  }))
}

module.exports = render
