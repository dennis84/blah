var snabbdom = require('snabbdom')
var patch = snabbdom.init([
  require('snabbdom/modules/class'),
  require('snabbdom/modules/props'),
  require('snabbdom/modules/style'),
  require('snabbdom/modules/eventlisteners'),
])
var h = require('snabbdom/h')

function render() {
  return h('h1', 'Jobs')
}

function Jobs(node) {
  var vnode = render()
  setTimeout(function() {
    patch(node, vnode)
  }, 0)
}

module.exports = Jobs
