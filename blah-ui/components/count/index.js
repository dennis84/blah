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

  function doUpdate(data) {
    state = data
    vnode = patch(vnode, render(data, update, options))
    if(typeof options.update === 'function') {
      options.update()
    }
  }

  function update(fn) {
    var args = [].slice.call(arguments, 1)
    var res = fn.apply(null, [state].concat(args))

    if(res instanceof Promise) {
      res.then(doUpdate)
    } else {
      doUpdate(res)
    }
  }

  patch(node, vnode)
  update(ctrlFn, options)

  conn.on('user', function(data) {
    update(ctrlFn, options)
  })
}

module.exports = {
  Num: Main.bind(null, require('./num'), ctrl.count),
  Bar: Main.bind(null, require('./bar'), ctrl.grouped),
  Line: Main.bind(null, require('./line'), ctrl.grouped),
  Pie: Main.bind(null, require('./pie'), ctrl.grouped),
  Diff: Main.bind(null, require('./diff'), ctrl.countDiff)
}
