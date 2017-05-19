var h = require('snabbdom/h').default

function container(model, children) {
  return h('div.container', children)
}

module.exports = container
