var snabbdom = require('snabbdom')
var patch = snabbdom.init([
  require('snabbdom/modules/class'),
  require('snabbdom/modules/props'),
  require('snabbdom/modules/style'),
  require('snabbdom/modules/eventlisteners')
])
var ctrl = require('./ctrl')
var render = require('./render')

function Sum(node, ws, options) {
  var state = {sum: 0}
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

  function onCount(data) {
    update(ctrl.sum, options)
  }

  patch(node, vnode)
  update(ctrl.sum, options)
  ws.on('count', onCount)

  this.destroy = function() {
    ws.removeListener('count', onCount)
  }
}

module.exports = Sum