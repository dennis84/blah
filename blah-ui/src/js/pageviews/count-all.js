import {h} from 'virtual-dom'
import {countAll} from './ctrl'
import hook from '../hook'

function views(xs) {
  if(undefined === xs) return
  return xs.map((x) => {
    return h('div.view', [
      h('div', 'page: ' + x.name),
      h('div', 'count: ' + String(x.count))
    ])
  })
}

function render(model, update, id, options) {
  return h('div.widget', {
    init: hook((node) => {
      if(null === id) update(countAll, options)
    })
  }, [
    h('h3', 'All Pageviews'),
    h('div', views(model.views))
  ])
}

export default render
