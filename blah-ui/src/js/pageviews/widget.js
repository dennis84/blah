import {h} from 'virtual-dom'
import {query, incr} from './ctrl'
import hook from '../hook'

function render(model, update) {
  return h('h2', {
    init: hook((node) => {
      if(0 === Object.keys(model).length) update(query)
    }),
    onclick: (e) => update(incr)
  }, 'Widget' + model.count)
}

export default render
