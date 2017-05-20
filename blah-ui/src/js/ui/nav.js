var h = require('snabbdom/h').default
var ctrl = require('../ctrl')

function render(model, update) {
  return h('nav.level.nav', [
    h('span.nav-toggle', {
      class: {'is-active': model.isNavVisble},
      on: {click: [update, ctrl.toggleNav]}
    }, [
      h('span'), h('span'), h('span')
    ]),
    h('div.level-left.nav-menu', {
      class: {'is-active': model.isNavVisble}
    }, [
      h('a.nav-item', {props: {href: '#/pageviews'}}, 'Pageviews'),
      h('a.nav-item', {props: {href: '#/user'}}, 'User Stats'),
      h('a.nav-item', {props: {href: '#/misc'}}, 'Misc'),
      h('a.nav-item', {props: {href: '#/people'}}, 'People'),
      h('a.nav-item', {props: {href: '#/funnel'}}, 'Funnel'),
      h('a.nav-item', {props: {href: '#/segmentation'}}, 'Segmentation'),
      h('a.nav-item', {props: {href: '#/world-map'}}, 'Map'),
      h('div.divider'),
      h('div.dropdown.control', [
        h('a.dropdown-toggle.nav-item', {
          on: {click: [update, ctrl.toggleDropdown]}
        }, [h('i.material-icons', 'settings')]),
        h('div.nav-menu.dropdown-menu', {
          class: {'is-active': model.isDropdownVisble}
        }, [
          h('a.nav-item.dropdown-item', {props: {href: '#/jobs'}}, 'Chronos Jobs'),
        ])
      ])
    ])
  ])
}

module.exports = render
