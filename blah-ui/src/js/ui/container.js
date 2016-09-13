import h from 'snabbdom/h'

function container(model, children = []) {
  var theme = (vnode) => {
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

export default container
