import h from 'snabbdom/h'
import Masonry from 'masonry-layout'
import debounce from 'debounce'

function render(options = {}, items = []) {
  var masonryFn = debounce((vnode) => {
    if(undefined === vnode.masonry) {
      vnode.masonry = new Masonry(vnode.elm, {
        itemSelector: options.itemSelector,
        transitionDuration: 0
      })
    } else {
      vnode.masonry.reloadItems()
      vnode.masonry.layout()
    }
  }, 100)

  return h('div', {
    props: {className: options.className},
    hook: {
      insert: masonryFn,
      update: masonryFn
    }
  }, items)
}

export default render
