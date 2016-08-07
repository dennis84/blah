import {h} from 'virtual-dom'
import {theme} from '../ctrl'

function render(model, update, conn, storage) {
  return h('nav.level.nav', [
    h('span.nav-toggle', {
      onclick: e => {
        var elem = e.currentTarget
        var display = elem.classList.contains('is-active') ? 'none' : 'block'
        elem.classList.toggle('is-active')
        elem.parentNode.querySelector('.nav-menu').style.display = display
      }
    }, [
      h('span'), h('span'), h('span')
    ]),
    h('div.level-left.nav-menu', [
      h('a.nav-item.button.is-link', {href: '#/pageviews'}, 'Pageviews'),
      h('a.nav-item.button.is-link', {href: '#/user'}, 'User Stats'),
      h('a.nav-item.button.is-link', {href: '#/misc'}, 'Misc'),
      h('a.nav-item.button.is-link', {href: '#/people'}, 'People'),
      h('a.nav-item.button.is-link', {href: '#/funnel'}, 'Funnel'),
      h('a.nav-item.button.is-link', {href: '#/segmentation'}, 'Segmentation'),
      h('a.nav-item.button.is-danger.is-outlined', {href: '#/world-map'}, 'Map')
    ]),
    h('div.level-right.nav-menu', [
      h('a.button.is-link', {href: '#/jobs'}, 'Jobs'),
      h('a.button.is-link', {
        onclick: node => update('theme', storage)
      }, h('i.material-icons', 'color_lens'))
    ])
  ])
}

export default render
