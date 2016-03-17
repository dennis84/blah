import {h} from 'virtual-dom'

function render(model, chan, conn) {
  return h('div.nav', [
    h('div.nav-item', h('a.button.is-link', {href: '#/pageviews'}, 'Pageviews')),
    h('div.nav-item', h('a.button.is-link', {href: '#/user'}, 'User Stats')),
    h('div.nav-item', h('a.button.is-link', {href: '#/misc'}, 'Misc')),
    h('div.nav-item', h('a.button.is-link', {href: '#/custom'}, 'Custom')),
    h('div.nav-item', h('a.button.is-red', {href: '#/builder'}, 'Widget Builder'))
  ])
}

export default render
