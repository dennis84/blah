import csp from 'js-csp'
import equal from 'deep-equal'

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
      this.model = this.state[prev.id]
      this.id = prev.id

      if(shouldUpdate(this, prev)) {
        return this.renderWidget()
      }

      return prev.vnode
    }

    this.id = 1

    return this.renderWidget()
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
      })
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
  if(prev.model !== curr.state[prev.id]) return true
  return !equal(prev.model, curr.state[prev.id])
}

function widget(fn, state, channel, ...args) {
  return new Widget(fn, state, channel, args)
}

export default widget
