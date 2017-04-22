var h = require('snabbdom/h').default
var debounce = require('debounce')
var ctrl = require('./ctrl')

function mkItems(xs) {
  if(undefined === xs || 0 == xs.length) return []
  return [h('div.list', xs.map(function(item) {
    return h('div.list-item.level.is-bordered', [
      h('div.level-left', [h('span', item.item)]),
      h('div.level-right', [
        h('span.is-pulled-right.tag.is-danger',
          String(parseFloat(item.score).toFixed(2)))
      ])
    ])
  }))]
}

function render(model, update, options) {
  return h('div.widget.is-borderless.widget-recommendation', [
    h('div.is-bordered', [
      h('h3', 'Recommendations'),
      h('div.field', [
        h('p.control', [
          h('input.input.is-medium', {
            props: {
              placeholder: 'Enter username',
              value: options.user
            },
            on: {
              input: debounce(function(e) {
                if(!e.target.value) return
                update(ctrl.find, {
                  baseUrl: options.baseUrl,
                  user: e.target.value
                })
              }, 500)
            }
          })
        ])
      ])
    ]),
  ].concat(mkItems(model.items)))
}

module.exports = render
