import {h} from 'virtual-dom'
import {grouped} from './ctrl'
import hook from '../hook'

function groups(xs) {
  if(undefined === xs) return
  return xs.map((x) => {
    return h('div.view', Object.keys(x).map((key) => {
      return h('div', key + ': ' + String(x[key]))
    }))
  })
}

function render(model, update, id, options) {
  return h('div.widget', {
    init: hook((node) => {
      if(null === id) update(grouped, options)
    })
  }, [
    h('h3', 'Grouped Pageviews'),
    h('div', groups(model.groups))
  ])
}

export default render
