var snabbdom = require('snabbdom')
var patch = snabbdom.init([
  require('snabbdom/modules/class').default,
  require('snabbdom/modules/props').default,
  require('snabbdom/modules/style').default,
  require('snabbdom/modules/eventlisteners').default,
])
var ctrl = require('./ctrl')
var render = require('./render')

function People(node, options) {
  options.baseUrl = options.baseUrl || ''
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
}

module.exports = People
