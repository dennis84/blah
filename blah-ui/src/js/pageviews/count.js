import {h} from 'virtual-dom'
import {count, incr} from './ctrl'
import hook from '../hook'

function render(model, update, id, options) {
  return h('div.widget.widget-count', {
    init: hook((node) => {
      if(null === id) update(count, options)
    }),
    onclick: (e) => update(incr)
  }, h('div.wrapper', [
    h('div.count', String(model.count)),
    h('div.page', options.filterBy.page)
  ]))
}

export default render
