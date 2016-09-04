var snabbdom = require('snabbdom')
var patch = snabbdom.init([
  require('snabbdom/modules/class'),
  require('snabbdom/modules/props'),
  require('snabbdom/modules/style'),
  require('snabbdom/modules/eventlisteners'),
])
var h = require('snabbdom/h')
var ctrl = require('./ctrl')
var render = require('./render')

function Jobs(node) {
  var state = {}
  var vnode = render(state, update)

  function update(fn) {
    var args = [].slice.call(arguments, 1)
    var res = fn.apply(null, [state].concat(args))

    if(res instanceof Promise) {
      res.then(function(m) {
        state = m
        vnode = patch(vnode, render(m, update))
      })
    } else {
      state = res
      vnode = patch(vnode, render(res, update))
    }
  }

  patch(node, vnode)
  update(ctrl.list)

  setInterval(function() {
    update(ctrl.list)
  }, 5000)
}

module.exports = Jobs
