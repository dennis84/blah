class Component {
  constructor(main, args) {
    this.shouldUpdate = true
    this.main = main
    this.args = args
    this.fn = null
    this.type = 'Thunk'
  }

  render(prev) {
    if(!prev) {
      this.fn = this.main.apply(this, this.args)
    } else {
      this.fn = prev.fn
    }

    if(!this.shouldUpdate) return prev.vnode
    this.shouldUpdate = false
    return this.fn.apply(null)
  }
}

function component(main, ...args) {
  return new Component(main, args)
}

export default component
