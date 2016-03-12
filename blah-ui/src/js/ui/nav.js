import {h} from 'virtual-dom'

function render(model, chan, conn) {
  return h('div.nav', [
    h('div.nav-item', h('a', {href: '#/pageviews'}, 'Pageviews')),
    h('div.nav-item', h('a', {href: '#/user'}, 'User Stats')),
    h('div.nav-item', h('a', {href: '#/misc'}, 'Misc'))
  ])
}

export default render
