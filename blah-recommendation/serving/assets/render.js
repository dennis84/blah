var h = require('snabbdom/h').default
var debounce = require('debounce')
var ctrl = require('./ctrl')

function render(model, update, options) {
  var items = model.items || []
  return h('div.recommendation.widget', [
    h('div.box', [
      h('h3.title', 'Find Recommended Items by User'),
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
  ].concat(items.map(function(item) {
    return h('div.box.level', [
      h('div.level-left', h('span', item.item)),
      h('div.level-right', [
        h('span.is-pulled-right.tag.is-danger',
          String(parseFloat(item.score).toFixed(2)))
      ])
    ])
  })))
}

module.exports = render
