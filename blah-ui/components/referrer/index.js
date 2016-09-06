var snabbdom = require('snabbdom')
var patch = snabbdom.init([
  require('snabbdom/modules/class'),
  require('snabbdom/modules/props'),
  require('snabbdom/modules/style'),
  require('snabbdom/modules/eventlisteners'),
])
var h = require('snabbdom/h')
var WebSocketListener = require('../util/websocket')
var ctrl = require('./ctrl')
var render = require('./render')

function Referrer(node, ws, options) {
  var conn = new WebSocketListener(ws)
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
  update(ctrl.search, options)

  conn.on('referrer', function(data) {
    update(ctrl.search, options)
  })
}

module.exports = Referrer
