import {h} from 'virtual-dom'

class Component {
  constructor(Fn, options = {}) {
    this.type = 'Widget'
    this.Fn = Fn
    this.options = options
  }

  init() {
    var elem = document.createElement('div')
    var Fn = this.Fn
    var options = this.options
    setTimeout(function() {
      new Fn(elem, options)
    }, 0)

    return elem
  }

  update() {
  }

  destroy() {
  }
}

function component(Fn, attrs = {}, options = {}) {
  return h('div', attrs, new Component(Fn, options))
}

export default component
