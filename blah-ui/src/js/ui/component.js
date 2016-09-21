var h = require('snabbdom/h')
var xtend = require('xtend')

function component(Fn, attrs) {
  var args = [].slice.call(arguments, 2)
  return h('div', xtend({
    hook: {
      insert: function(vnode) {
        var ComponentFn = Fn.bind.apply(Fn, [null, vnode.elm].concat(args))
        vnode.component = new ComponentFn
      },
      update: function(oldVnode, vnode) {
        vnode.component = oldVnode.component
      },
      destroy: function(vnode) {
        if(vnode.component && vnode.component.destroy) {
          vnode.component.destroy()
        }
      }
    }
  }, attrs))
}

module.exports = component
