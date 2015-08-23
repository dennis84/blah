class Component {
  constructor(...args) {
    this.args = args
    this.type = 'Thunk'
    this.state = null
    this.fn = null
  }

  render(prev) {
    if(!prev) {
      this.fn = this.initialize.apply(this, this.args)
    } else {
      this.fn = prev.fn
      this.state = prev.state
    }

    return this.fn.apply(this)
  }
}

export default Component
