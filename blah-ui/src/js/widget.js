import csp from 'js-csp'
import equal from 'deep-equal'
import uuid from 'uuid'

class Widget {
  constructor(fn, state, channel, args) {
    this.fn = fn
    this.state = state
    this.channel = channel
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
    var resp = fn.apply(null, [this.model].concat(args))
    csp.go(function*() {
      resp.then((m) => {
        csp.putAsync(that.channel, {
          type: 'widget',
          args: [that.id, m]
        })
      }).catch((r) => {})
    })
  }

  renderWidget() {
    return this.fn.apply(null, [
      this.model,
      this.update.bind(this)
    ].concat(this.args))
  }
}

function shouldUpdate(curr, prev) {
  if(prev.model === curr.model) return false
  return !equal(prev.model, curr.model)
}

function widget(fn, state, channel, ...args) {
  return new Widget(fn, state, channel, args)
}

export default widget
