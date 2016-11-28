var snabbdom = require('snabbdom')
var patch = snabbdom.init([
  require('snabbdom/modules/class'),
  require('snabbdom/modules/props'),
  require('snabbdom/modules/style'),
  require('snabbdom/modules/eventlisteners'),
])
var ctrl = require('./ctrl')

function Main(render, ctrlFn, node, ws, options) {
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

  function onUser() {
    update(ctrlFn, options)
  }

  patch(node, vnode)
  update(ctrlFn, options)
  ws.on('user', onUser)

  this.destroy = function() {
    ws.removeListener('user', onUser)
  }
}

module.exports = {
  Count: Main.bind(null, require('./count'), ctrl.count),
  Bar: Main.bind(null, require('./bar'), ctrl.grouped)
}
