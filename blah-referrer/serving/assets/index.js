var snabbdom = require('snabbdom')
var patch = snabbdom.init([
  require('snabbdom/modules/class'),
  require('snabbdom/modules/props'),
  require('snabbdom/modules/style'),
  require('snabbdom/modules/eventlisteners'),
])
var ctrl = require('./ctrl')
var render = require('./render')

function Referrer(node, events, options) {
  options.baseUrl = options.baseUrl || ''
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

  function onReferrer() {
    update(ctrl.search, options)
  }

  patch(node, vnode)
  update(ctrl.search, options)
  events.on('referrer', onReferrer)

  this.destroy = function() {
    events.removeListener('referrer', onReferrer)
  }
}

module.exports = Referrer
