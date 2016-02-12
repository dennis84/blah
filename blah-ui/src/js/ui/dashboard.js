import {h} from 'virtual-dom'
import Masonry from 'masonry-layout'
import debounce from 'debounce'
import {hook} from '../hook'
import makeWidgets from './widgets'

function render(model, chan, conn) {
  return h('div.container', [
    h('h1.center-h', 'Dashboard'),
    h('div.widgets', {
      hook: hook(debounce((node) => {
        if(undefined === node.masonry) {
          node.masonry = new Masonry(node, {
            itemSelector: '.widget'
          })
        } else {
          node.masonry.reloadItems()
          node.masonry.layout()
        }
      }), 100)
    }, makeWidgets(model, chan, conn))
  ])
}

export default render
