import equal from 'deep-equal'
import uuid from 'uuid'

class Widget {
  constructor(fn, state, updateFn, initial, args) {
    this.fn = fn
    this.state = state
    this.updateFn = updateFn
    this.args = args
    this.model = initial
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
    var res = fn(this.model, ...args)
    if(res instanceof Promise) {
      res.then((m) => that.updateFn('widget', that.id, m)).catch((r) => that.updateFn('error', r))
    } else {
      that.updateFn('widget', that.id, res)
    }
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

function widget(fn, state, updateFn, initial = {}, ...args) {
  if(state.error) return 
  return new Widget(fn, state, updateFn, initial, args)
}

export default widget
