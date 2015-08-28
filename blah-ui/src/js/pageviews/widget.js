import {h} from 'virtual-dom'
import {query, incr} from './ctrl'
import hook from '../hook'

function render(model, update, id, options) {
  return h('h2', {
    init: hook((node) => {
      if(null === id) update(query, options)
    }),
    onclick: (e) => update(incr)
  }, 'Widget' + model.count)
}

export default render
