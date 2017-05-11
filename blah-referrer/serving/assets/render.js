var h = require('snabbdom/h')

function render(model) {
  var referrers = model.referrers || []
  return h('div.referrer.widget', [
    h('div.box', [
      h('h3.title', 'Referring Sites')
    ]),
  ].concat(referrers.map(function(referrer) {
    return h('div.box.level', [
      h('div.level-left', [
        h('span', referrer.url)
      ]),
      h('div.level-right', [
        h('span.tag.is-danger', String(referrer.count))
      ])
    ])
  })))
}

module.exports = render
