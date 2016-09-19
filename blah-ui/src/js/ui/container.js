var h = require('snabbdom/h')

function container(model, children) {
  var theme = function(vnode) {
    var html = document.documentElement
    html.className = ''
    html.classList.add(model.theme)
  }

  return h('div.container', {
    hook: {
      insert: theme,
      update: theme
    }
  }, children)
}

module.exports = container
