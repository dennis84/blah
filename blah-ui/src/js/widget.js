import csp from 'js-csp'
import equal from 'deep-equal'
import uuid from 'uuid'

class Widget {
  constructor(fn, state, updateFn, args) {
    this.fn = fn
    this.state = state
    this.updateFn = updateFn
    this.args = args
    this.model = {}
    this.type = 'Thunk'
    this.id = null
  }

  render(prev) {
    if(prev && null !== prev.id) {
      if(undefined !== this.state[prev.id]) {
        this.model = this.state[prev.id]
      }

      this.id = prev.id

      if(shouldUpdate(this, prev)) {
        return this.renderWidget()
      }

      return prev.vnode
    }

    var vnode = this.renderWidget()
    this.id = uuid.v4()

    return vnode
  }

  update(fn, ...args) {
    var that = this
    fn(this.model, ...args)
      .then((m) => that.updateFn('widget', that.id, m))
      .catch((r) => {})
  }

  renderWidget() {
    return this.fn(this.model, this.update.bind(this), ...this.args)
  }
}

function shouldUpdate(curr, prev) {
  if(prev.fn !== curr.fn) return true
  if(!equal(prev.args, curr.args)) return true
  if(!equal(prev.model, curr.model)) return true
  return false
}

function widget(fn, state, updateFn, ...args) {
  return new Widget(fn, state, updateFn, args)
}

export default widget
