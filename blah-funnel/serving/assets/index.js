var snabbdom = require('snabbdom')
var patch = snabbdom.init([
  require('snabbdom/modules/class').default,
  require('snabbdom/modules/props').default,
  require('snabbdom/modules/style').default,
  require('snabbdom/modules/eventlisteners').default,
])
var ctrl = require('./ctrl')
var render = require('./render')

function Funnel(node, events, options) {
  options.baseUrl = options.baseUrl || ''
  var state = {activeTab: 'bar'}
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

  function onFunnel() {
    update(ctrl.search, options)
  }

  patch(node, vnode)
  update(ctrl.search, options)
  events.on('funnel', onFunnel)

  this.destroy = function() {
    events.removeListener('funnel', onFunnel)
  }
}

module.exports = Funnel
