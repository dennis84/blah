import h from 'snabbdom/h'

function unknown() {
  return h('div.has-text-centered', [
    h('i.material-icons.is-large', 'cloud_off'),
    h('p', 'No connection'),
    h('a.button.is-danger.is-outlined', {
      on: {click: () => window.location.reload()}
    }, 'Retry')
  ])
}

function noData() {
  return h('div.has-text-centered', [
    h('i.material-icons.is-large', 'error_outline'),
    h('p', 'No data')
  ])
}

export {
  unknown,
  noData
}
