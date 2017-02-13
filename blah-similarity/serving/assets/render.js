var h = require('snabbdom/h').default
var debounce = require('debounce')
var ctrl = require('./ctrl')

function mkSimilarities(xs) {
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
  return h('div.widget.is-borderless.widget-similarity', [
    h('div.is-bordered', [
      h('h3', 'Similarity'),
      h('div.control', [
        h('input.input', {
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
    ].concat(mkSimilarities(model.similarities)))
  ])
}

module.exports = render
