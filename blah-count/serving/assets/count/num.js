var h = require('snabbdom/h').default

function render(model, update, options) {
  var prevs = model.prevs || []
  return h('div.count-num.box.widget', [
    h('div.has-text-centered', [
      h('div.value', String(model.count)),
      h('div.title', options.title)
    ]),
    h('div.columns.is-multiline', prevs.map(function(prev) {
      var pct = Math.round(model.count / prev.count * 100 - 100)
      var icon = pct < 0 ? 'trending_down' : 'trending_up'
      return h('div.column.is-half.has-text-centered', [
        h('div', [
          h('span.prev-value', String(prev.count)),
          h('span.prev-trend', [
            h('span.tag.is-danger.icon.is-small', {
              class: {'is-danger': pct < 0, 'is-primary': pct >= 0}
            }, h('i.material-icons', icon)),
            h('span', String(Math.abs(pct)) + '%'),
          ]),
        ]),
        h('div.prev-title', prev.title),
      ])
    }))
  ])
}

module.exports = render
