import {h} from 'virtual-dom'

class Component {
  constructor(Fn, ...args) {
    this.type = 'Widget'
    this.Fn = Fn
    this.args = args
  }

  init() {
    var elem = document.createElement('div')
    var Fn = this.Fn
    var args = this.args
    setTimeout(() => new Fn(elem, ...args), 0)
    return elem
  }

  update() {
  }

  destroy() {
  }
}

function component(Fn, attrs = {}, ...args) {
  return h('div', attrs, new Component(Fn, ...args))
}

export default component
