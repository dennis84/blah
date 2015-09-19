import {h} from 'virtual-dom'
import hook from '../hook'
import {recommendations} from './ctrl'

function views(xs) {
  if(undefined === xs) return
  return xs.map((item) => {
    return h('div', [
      h('div', 'page: ' + item.page),
      h('div', 'score: ' + item.score)
    ])
  })
}

function render(model, update, id, options) {
  return h('div.widget.widget-recommendations', {
    init: hook((node) => {
      if(null === id) update(recommendations, options)
    })
  }, [
    h('h3', 'Recommendations'),
    h('div', 'user: ' + options.user),
    h('div', views(model.views))
  ])
}

export default render
