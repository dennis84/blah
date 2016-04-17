import {h} from 'virtual-dom' 

function unknown() {
  return h('div.is-text-centered', [
    h('i.material-icons.is-large', 'cloud_off'),
    h('p', 'No connection'),
    h('a.button.is-danger.is-outlined', {
      onclick: () => window.location.reload()
    }, 'Retry')
  ])
}

function noData() {
  return h('div.is-text-centered', [
    h('i.material-icons.is-large', 'error_outline'),
    h('p', 'No data')
  ])
}

export {
  unknown,
  noData
}
