import h from 'snabbdom/h'
import xtend from 'xtend'

function component(Fn, attrs = {}, ...args) {
  return h('div', xtend({
    hook: {
      insert: (vnode) => {
        new Fn(vnode.elm, ...args)
      }
    }
  }, attrs))
}

export default component
