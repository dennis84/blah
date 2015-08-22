function Component(main, args) {
  this.shouldUpdate = true
  this.main = main
  this.args = args
  this.fn = null
}

Component.prototype.type = 'Thunk'
Component.prototype.render = render

function render(prev) {
  if(!prev) {
    this.fn = this.main.apply(this, this.args)
  } else {
    this.fn = prev.fn
  }

  if(!this.shouldUpdate) return prev.vnode
  this.shouldUpdate = false
  return this.fn.apply(null)
}

function component(main) {
  var args = [].slice.call(arguments, 1)
  return new Component(main, args)
}

module.exports = component
