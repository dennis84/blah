import csp from 'js-csp'
import equal from 'deep-equal'

class Widget {
  constructor(fn, state, channel) {
    this.fn = fn
    this.state = state
    this.channel = channel
    this.model = {}
    this.type = 'Thunk'
    this.id = null
  }

  render(prev) {
    if(prev && null !== prev.id) {
      this.model = this.state[prev.id]
      this.id = prev.id

      if(shouldUpdate(this, prev)) {
        return this.fn.apply(null, [this.model, this.update.bind(this)])
      }

      return prev.vnode
    }

    this.id = 1

    return this.fn.apply(null, [this.model, this.update.bind(this)])
  }


  update(fn) {
    var that = this
    var resp = fn.call(null, this.model)
    csp.go(function*() {
      resp.then((m) => {
        csp.putAsync(that.channel, {
          type: 'widget',
          args: [that.id, m]
        })
      })
    })
  }
}

function shouldUpdate(curr, prev) {
  if(prev.model !== curr.state[prev.id]) return true
  return !equal(prev.model, curr.state[prev.id])
}

function widget(fn, state, channel) {
  return new Widget(fn, state, channel)
}

export default widget
