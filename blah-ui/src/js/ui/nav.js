import {h} from 'virtual-dom'

function render(model, chan, conn) {
  return h('nav.navbar', h('div.navbar-left', [
    h('p.navbar-item', h('a.button.is-link', {href: '#/pageviews'}, 'Pageviews')),
    h('p.navbar-item', h('a.button.is-link', {href: '#/user'}, 'User Stats')),
    h('p.navbar-item', h('a.button.is-link', {href: '#/misc'}, 'Misc')),
    h('p.navbar-item', h('a.button.is-link', {href: '#/people'}, 'People')),
    h('p.navbar-item', h('a.button.is-danger.is-outlined', {href: '#/segmentation'}, 'Segmentation'))
  ]))
}

export default render
