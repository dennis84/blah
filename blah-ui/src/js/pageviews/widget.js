import {h} from 'virtual-dom'
import {query, incr} from './ctrl'
import hook from '../hook'

function render(model, update, options) {
  return h('h2', {
    init: hook((node) => {
      if(0 === Object.keys(model).length) update(query, options)
    }),
    onclick: (e) => update(incr)
  }, 'Widget' + model.count)
}

export default render
