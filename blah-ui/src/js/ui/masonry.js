import h from 'snabbdom/h'
import Masonry from 'masonry-layout'
import debounce from 'debounce'

function render(options = {}, items = []) {
  return h('div', {
    props: {className: options.className},
    hook: {
      insert: debounce((vnode) => {
        if(undefined === vnode.elm.masonry) {
          vnode.elm.masonry = new Masonry(vnode.elm, {
            itemSelector: options.itemSelector,
            transitionDuration: 0
          })
        } else {
          vnode.elm.masonry.reloadItems()
          vnode.elm.masonry.layout()
        }
      }, 100)
    }
  }, items)
}

export default render
