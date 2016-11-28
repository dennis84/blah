var snabbdom = require('snabbdom')
var patch = snabbdom.init([
  require('snabbdom/modules/class'),
  require('snabbdom/modules/props'),
  require('snabbdom/modules/style'),
  require('snabbdom/modules/eventlisteners'),
])
var render = require('./render')
var ctrl = require('./ctrl')

function Autoscale(node, options) {
  var state = {apps: []}
  var vnode = render(state, update, options)

  function update(fn) {
    var args = [].slice.call(arguments, 1)
    var res = fn.apply(null, [state].concat(args))
    state = res
    vnode = patch(vnode, render(res, update, options))
  }

  var source = new EventSource('http://autoscale.blah.local')
  source.onmessage = function(e) {
    update(ctrl.update, JSON.parse(e.data))
  }

  patch(node, vnode)
}

module.exports = Autoscale
