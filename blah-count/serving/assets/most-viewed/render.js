var h = require('snabbdom/h').default

function render(model) {
  var items = model.items || []
  return h('div.most-viewed.widget', [
    h('div.box', [
      h('h3.title', 'Most Viewed Items'),
    ]),
  ].concat(items.map(function(item, index) {
    return h('div.box.level', [
      h('div.level-left', [
        h('span', (index + 1) + ' - ' + item.item)
      ]),
      h('div.level-right', [
        h('span.tag.is-danger', String(item.count))
      ])
    ])
  })))
}

module.exports = render
