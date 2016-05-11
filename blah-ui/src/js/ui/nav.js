import {h} from 'virtual-dom'
import {theme} from '../ctrl'

function render(model, update, conn, storage) {
  return h('nav.level.navbar', [
    h('div.level-left', [
      h('a.button.is-link', {href: '#/pageviews'}, 'Pageviews'),
      h('a.button.is-link', {href: '#/user'}, 'User Stats'),
      h('a.button.is-link', {href: '#/misc'}, 'Misc'),
      h('a.button.is-link', {href: '#/people'}, 'People'),
      h('a.button.is-link', {href: '#/segmentation'}, 'Segmentation'),
      h('a.button.is-danger.is-outlined', {href: '#/world-map'}, 'Map')
    ]),
    h('div.level-right', [
      h('a.button.is-link', {
        onclick: node => update('theme', storage)
      }, h('i.material-icons', 'color_lens'))
    ])
  ])
}

export default render
