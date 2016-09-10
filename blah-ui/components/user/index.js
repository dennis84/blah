var snabbdom = require('snabbdom')
var patch = snabbdom.init([
  require('snabbdom/modules/class'),
  require('snabbdom/modules/props'),
  require('snabbdom/modules/style'),
  require('snabbdom/modules/eventlisteners'),
])
var h = require('snabbdom/h')
var listen = require('./websocket')
var ctrl = require('./ctrl')

function Main(render, ctrlFn, node, ws, options) {
  var conn = listen(ws)
  var state = {}
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
    }
  }

  patch(node, vnode)
  update(ctrlFn, options)

  conn.on('user', function(data) {
    update(ctrlFn, options)
  })
}

module.exports = {
  Count: Main.bind(null, require('./count'), ctrl.count),
  Bar: Main.bind(null, require('./bar'), ctrl.grouped)
}
