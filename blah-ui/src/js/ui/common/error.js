import {h} from 'virtual-dom' 

function unknown() {
  return h('div.center-hv', h('span', [
    h('i.material-icons.is-large', 'cloud_off'),
    h('p', 'No connection'),
    h('a.button.is-red', {
      onclick: () => window.location.reload()
    }, 'Retry'),
  ]))
}

function error(model) {
  if(!model.error) return
  return unknown();
}

export default error
