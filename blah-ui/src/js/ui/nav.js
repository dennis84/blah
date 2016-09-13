import h from 'snabbdom/h'
import {theme} from '../ctrl'

function render(model, update, conn, storage) {
  return h('nav.level.nav', [
    h('span.nav-toggle', {
      on: {click: (e) => {
        var elem = e.currentTarget
        var display = elem.classList.contains('is-active') ? 'none' : 'block'
        elem.classList.toggle('is-active')
        elem.parentNode.querySelector('.nav-menu').style.display = display
      }}
    }, [
      h('span'), h('span'), h('span')
    ]),
    h('div.level-left.nav-menu', [
      h('a.nav-item.button.is-link', {props: {href: '#/pageviews'}}, 'Pageviews'),
      h('a.nav-item.button.is-link', {props: {href: '#/user'}}, 'User Stats'),
      h('a.nav-item.button.is-link', {props: {href: '#/misc'}}, 'Misc'),
      h('a.nav-item.button.is-link', {props: {href: '#/people'}}, 'People'),
      h('a.nav-item.button.is-link', {props: {href: '#/funnel'}}, 'Funnel'),
      h('a.nav-item.button.is-link', {props: {href: '#/segmentation'}}, 'Segmentation'),
      h('a.nav-item.button.is-link', {props: {href: '#/world-map'}}, 'Map')
    ]),
    h('div.level-right.nav-menu', [
      h('a.button.is-link', {props: {href: '#/jobs'}}, 'Jobs'),
      h('a.button.is-link', {
        on: {click: node => update(theme, storage)}
      }, [h('i.material-icons', 'color_lens')])
    ])
  ])
}

export default render
