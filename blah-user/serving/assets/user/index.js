var snabbdom = require('snabbdom')
var patch = snabbdom.init([
  require('snabbdom/modules/class').default,
  require('snabbdom/modules/props').default,
  require('snabbdom/modules/style').default,
  require('snabbdom/modules/eventlisteners').default,
])
var ctrl = require('./ctrl')

function Main(render, ctrlFn, node, events, options) {
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
  events.on('user', onUser)

  this.destroy = function() {
    events.removeListener('user', onUser)
  }
}

module.exports = {
  Count: Main.bind(null, require('./count'), ctrl.count),
  Bar: Main.bind(null, require('./bar'), ctrl.grouped)
}
