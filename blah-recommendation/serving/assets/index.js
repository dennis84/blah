var snabbdom = require('snabbdom')
var patch = snabbdom.init([
  require('snabbdom/modules/class').default,
  require('snabbdom/modules/props').default,
  require('snabbdom/modules/style').default,
  require('snabbdom/modules/eventlisteners').default
])
var ctrl = require('./ctrl')
var render = require('./render')

function Recommendation(node, options) {
  options.baseUrl = options.baseUrl || ''
  var state = {}
  var vnode = render(state, update, options)

  function doUpdate(data) {
    state = data
    vnode = patch(vnode, render(data, update, options))
    if(options.on && options.on.update) {
      options.on.update()
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

  if(options.user) {
    update(ctrl.find, options)
  }
}

module.exports = Recommendation
