var h = require('snabbdom/h').default
var ctrl = require('../ctrl')

function toggle(sel, e) {
  var elem = e.currentTarget
  var display = elem.classList.contains('is-active') ? 'none' : 'block'
  elem.classList.toggle('is-active')
  var menus = elem.parentNode.querySelectorAll(sel);
  [].forEach.call(menus, function(menu) {
    menu.style.display = display
  })
}

function render(model, update, storage) {
  return h('nav.level.nav', [
    h('span.nav-toggle', {
      on: {click: toggle.bind(null, '.nav-menu')}
    }, [
      h('span'), h('span'), h('span')
    ]),
    h('div.level-left.nav-menu', [
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
          on: {click: toggle.bind(null, '.dropdown-menu')}
        }, [h('i.material-icons', 'settings')]),
        h('div.nav-menu.dropdown-menu', [
          h('a.nav-item.dropdown-item', {props: {href: '#/jobs'}}, 'Chronos Jobs'),
          h('a.nav-item.dropdown-item', {props: {href: 'http://kibana.blah.local'}}, 'Kibana'),
          h('a.nav-item.dropdown-item', {
            on: {click: function() {
              update(ctrl.theme, storage)
            }}
          }, 'Toggle Theme')
        ])
      ])
    ])
  ])
}

module.exports = render
