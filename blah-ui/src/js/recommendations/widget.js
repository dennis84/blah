import {h} from 'virtual-dom'
import hook from '../hook'
import {recommendations} from './ctrl'

function views(xs) {
  if(undefined === xs) return
  return Object.keys(xs).map((key) => {
    return h('div', [
      h('div', 'page: ' + key),
      h('div', 'score: ' + xs[key])
    ])
  })
}

function render(model, update, id, options) {
  return h('div.widget.widget-recommendations', {
    init: hook((node) => {
      if(null === id) update(recommendations, options)
    })
  }, h('div.wrapper', [
    h('h3', 'Recommendations'),
    h('div', 'user: ' + options.user),
    h('div', views(model.views))
  ]))
}

export default render
