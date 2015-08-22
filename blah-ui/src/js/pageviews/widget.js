import {h} from 'virtual-dom'
var state = {
  i: 0
}

function main(conn) {
  var self = this
  setInterval(() => {
    self.shouldUpdate = true
    state.i ++
  }, 2000)

  return render.bind(null, state)
}

function render(s) {
  return h('h1', 'Pageviews ' + s.i)
}

module.exports = main
