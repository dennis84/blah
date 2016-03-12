import {h} from 'virtual-dom'
import Masonry from 'masonry-layout'
import debounce from 'debounce'
import {hook} from '../hook'

function render(options = {}, items = []) {
  return h('div', {
    className: options.className,
    hook: hook(debounce((node) => {
      if(undefined === node.masonry) {
        node.masonry = new Masonry(node, {
          itemSelector: options.itemSelector,
          transitionDuration: 0
        })
      } else {
        node.masonry.reloadItems()
        node.masonry.layout()
      }
    }), 100)
  }, items)
}

export default render
