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

function error(model) {
  if(!model.error) return
  return unknown();
}

export default error
