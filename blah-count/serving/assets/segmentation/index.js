var snabbdom = require('snabbdom')
var patch = snabbdom.init([
  require('snabbdom/modules/class').default,
  require('snabbdom/modules/props').default,
  require('snabbdom/modules/style').default,
  require('snabbdom/modules/eventlisteners').default,
])
var ctrl = require('./ctrl')
var render = require('./render')

function Segmentation(node, events, initial) {
  var state = initial
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
      update(ctrl.grouped, state)
    }
  }

  function onCount() {
    update(ctrl.grouped, state)
  }

  patch(node, vnode)
  update(ctrl.grouped, state)
  events.on('count', onCount)

  this.destroy = function() {
    events.removeListener('count', onCount)
  }
}

module.exports = Segmentation
