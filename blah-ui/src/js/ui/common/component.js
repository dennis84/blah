import {h} from 'virtual-dom'
import {mount} from '../../hook'

function component(model, update, Fn, options = {}) {
  return h('div', {
    className: options.className,
    mount: mount(node => new Fn(node))
  })
}

export default component
