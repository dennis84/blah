var h = require('snabbdom/h').default
var debounce = require('debounce')
var ctrl = require('./ctrl')

function render(model, update, options) {
  var similarities = model.similarities || []
  return h('div.similarity.widget', [
    h('div.box', [
      h('h3.title', 'Find Similar Items'),
      h('div.field', [
        h('p.control', [
          h('input.input.is-medium', {
            props: {
              placeholder: 'Enter item',
              value: options.item,
            },
            on: {
              input: debounce(function(e) {
                if(!e.target.value) return
                var items = e.target.value.split(',')
                update(ctrl.find, {
                  baseUrl: options.baseUrl,
                  items: items
                })
              }, 500)
            }
          })
        ])
      ])
    ]),
  ].concat(similarities.map(function(item) {
    return h('div.box.level', [
      h('div.level-left', [h('span', item.item)]),
      h('div.level-right', [
        h('span.is-pulled-right.tag.is-danger',
          String(parseFloat(item.score).toFixed(2)))
      ])
    ])
  })))
}

module.exports = render
