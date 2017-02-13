var h = require('snabbdom/h').default

function unknown() {
  return h('div.has-text-centered', [
    h('i.material-icons.is-large', 'cloud_off'),
    h('p', 'No connection'),
    h('a.button.is-danger.is-outlined', {
      on: {click: function() {
        window.location.reload()
      }}
    }, 'Retry')
  ])
}

function noData() {
  return h('div.has-text-centered', [
    h('i.material-icons.is-large', 'error_outline'),
    h('p', 'No data')
  ])
}

module.exports = {
  unknown: unknown,
  noData: noData
}
