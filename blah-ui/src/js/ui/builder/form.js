import {h} from 'virtual-dom'

function render(model, update, ...views) {
  if(undefined === model.builder) return
  return h('form', views.map(x => x(model.builder, update)))
}

export default render
