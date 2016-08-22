import {h} from 'virtual-dom'

class Component {
  constructor(Fn) {
    this.type = 'Widget'
    this.Fn = Fn
  }

  init() {
    var elem = document.createElement('div')
    new this.Fn(elem)
    return elem
  }

  update() {
  }

  destroy() {
  }
}

function component(Fn, options) {
  return h('div', options, new Component(Fn))
}

export default component
