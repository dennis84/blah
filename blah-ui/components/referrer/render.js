var h = require('snabbdom/h')

function mkReferrers(xs, update) {
  if(undefined === xs || 0 == xs.length) return []
  return h('div.list.referrers', xs.map(function(referrer) {
    return h('div.list-item.level.is-bordered', [
      h('div.level-left', [
        h('span', referrer.url)
      ]),
      h('div.level-right', [
        h('span.tag.is-primary', String(referrer.count))
      ])
    ])
  }))
}

function render(model, update, options) {
  return h('div.widget.is-borderless.widget-referrer-list', {
    props: {className: options.className}
  }, [
    h('div.is-bordered', [h('h3', options.title)]),
  ].concat(mkReferrers(model.referrers, update)))
}

module.exports = render
