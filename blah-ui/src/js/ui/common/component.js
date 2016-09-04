import {h} from 'virtual-dom'

class Component {
  constructor(Fn) {
    this.type = 'Widget'
    this.Fn = Fn
  }

  init() {
    var elem = document.createElement('div')
    var Fn = this.Fn
    setTimeout(function() {
      new Fn(elem)
    }, 0)

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
