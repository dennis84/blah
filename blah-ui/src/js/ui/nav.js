var h = require('snabbdom/h')
var ctrl = require('../ctrl')

function toggle(e) {
  var elem = e.currentTarget
  var display = elem.classList.contains('is-active') ? 'none' : 'block'
  elem.classList.toggle('is-active')
  var menus = elem.parentNode.querySelectorAll('.nav-menu');
  [].forEach.call(menus, function(menu) {
    menu.style.display = display
  })
}

function render(model, update, ws, storage) {
  return h('nav.level.nav', [
    h('span.nav-toggle', {
      on: {click: toggle}
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
      h('a.nav-item.button.is-link', {props: {href: '#/world-map'}}, 'Map'),
      h('div.divider'),
      h('div.dropdown.control', [
        h('span.nav-toggle', {
          on: {click: toggle}
        }, [
          h('span'), h('span'), h('span')
        ]),
        h('div.nav-menu', [
          h('a.nav-item.button.is-link', {props: {href: '#/jobs'}}, 'Chronos Jobs'),
          h('a.nav-item.button.is-link', {props: {href: 'http://kibana.blah.local'}}, 'Kibana'),
          h('a.nav-item.button.is-link', {
            on: {click: function(node) {
              update(ctrl.theme, storage)
            }}
          }, [h('i.material-icons', 'color_lens')])
        ])
      ])
    ])
  ])
}

module.exports = render
