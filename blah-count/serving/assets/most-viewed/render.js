var h = require('snabbdom/h').default

function mkItems(xs) {
  if(undefined === xs || 0 == xs.length) return []
  return h('div.list.most-viewed', xs.map(function(item, index) {
    return h('div.list-item.level.is-bordered', [
      h('div.level-left', [
        h('span', (index + 1) + ' - ' + item.item)
      ]),
      h('div.level-right', [
        h('span.tag.is-primary', String(item.count))
      ])
    ])
  }))
}

function render(model, update, options) {
  return h('div.widget.is-borderless.widget-most-viewed', {
    class: options.class
  }, [
    h('div.is-bordered', [h('h3', options.title)]),
  ].concat(mkItems(model.items, update)))
}

module.exports = render
