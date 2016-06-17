import {h} from 'virtual-dom'

function render(model, update, ...views) {
  return h('form', views.map(x => x(model, update)))
}

export default render
