var snabbdom = require('snabbdom')
var patch = snabbdom.init([
  require('snabbdom/modules/class'),
  require('snabbdom/modules/props'),
  require('snabbdom/modules/style'),
  require('snabbdom/modules/eventlisteners'),
])
var xtend = require('xtend')
var ctrl = require('./ctrl')
var render = require('./render')

function Segmentation(node, ws, options, initial) {
  var conn = listen(ws)
  var state = initial
  var vnode = render(state, update, options)

  function update(fn) {
    var args = [].slice.call(arguments, 1)
    var res = fn.apply(null, [state].concat(args))

    if(res instanceof Promise) {
      res.then(function(m) {
        state = m
        vnode = patch(vnode, render(m, update, options))
      })
    } else {
      state = res
      vnode = patch(vnode, render(res, update, options))
      update(ctrl.grouped, xtend(options, state))
    }
  }

  patch(node, vnode)
  update(ctrl.grouped, xtend(options, state))

  ws.on('count', function(data) {
    update(ctrl.grouped, xtend(options, state))
  })
}

module.exports = Segmentation
