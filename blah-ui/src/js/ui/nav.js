import {h} from 'virtual-dom'

function render(model, chan, conn) {
  return h('div.nav', [
    h('div.nav-item', h('a.button-link', {href: '#/pageviews'}, 'Pageviews')),
    h('div.nav-item', h('a.button-link', {href: '#/user'}, 'User Stats')),
    h('div.nav-item', h('a.button-link', {href: '#/misc'}, 'Misc')),
    h('div.nav-item', h('a.button-link', {href: '#/custom'}, 'Custom')),
    h('div.nav-item', h('a.button-red', {href: '#/builder'}, 'Widget Builder'))
  ])
}

export default render
