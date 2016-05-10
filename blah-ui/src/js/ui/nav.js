import {h} from 'virtual-dom'
import {theme} from '../ctrl'

function render(model, update, conn, storage) {
  return h('nav.navbar', [
    h('div.navbar-left', [
      h('p.navbar-item', h('a.button.is-link', {href: '#/pageviews'}, 'Pageviews')),
      h('p.navbar-item', h('a.button.is-link', {href: '#/user'}, 'User Stats')),
      h('p.navbar-item', h('a.button.is-link', {href: '#/misc'}, 'Misc')),
      h('p.navbar-item', h('a.button.is-link', {href: '#/people'}, 'People')),
      h('p.navbar-item', h('a.button.is-link', {href: '#/segmentation'}, 'Segmentation')),
      h('p.navbar-item', h('a.button.is-danger.is-outlined', {href: '#/world-map'}, 'Map'))
    ]),
    h('div.navbar-right', [
      h('p.navbar-item', h('a.button.is-link', {
        onclick: node => update('theme', storage)
      }, h('i.material-icons', 'color_lens')))
    ])
  ])
}

export default render
