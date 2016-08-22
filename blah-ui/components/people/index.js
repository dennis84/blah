var mainLoop = require('../util/main-loop')
var bindAppend = require('../util/bind-append')
var render = require('./render')
var ctrl = require('./ctrl')

function People(node) {
  var state = {}
  var renderFn = bindAppend(render, null, update)
  var loop = mainLoop(state, renderFn, node)

  function update(fn) {
    var args = [].slice.call(arguments, 1)
    var res = fn.apply(null, [loop.state].concat(args))

    if(res instanceof Promise) {
      res.then(function(m) {
        loop.update(m)
      })
    } else {
      loop.update(res)
    }
  }

  update(ctrl.search)
}

module.exports = People
