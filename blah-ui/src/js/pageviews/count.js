import {h} from 'virtual-dom'
import {count, incr} from './ctrl'
import hook from '../hook'

function render(model, update, id, options) {
  return h('div.widget', {
    init: hook((node) => {
      if(null === id) update(count, options)
    }),
    onclick: (e) => update(incr)
  }, [
    h('h3', 'Pageviews'),
    h('div', 'name: ' + options.filterBy.page),
    h('div', 'count: ' + model.count)
  ])
}

export default render
