import 'babelify/polyfill'
import {diff, patch} from 'virtual-dom'
import createElement from 'virtual-dom/create-element'
import csp from 'js-csp'
import connect from './connection'
import dashboard from './dashboard'

var conn = connect('ws://localhost:8001/ws')
var channel = csp.chan()
var model = {count: 0}

function update(model, action) {
  switch (action.type) {
    case 'incr':
      model.count += 1
      return model
    case 'widget':
      var id = action.args[0]
      var m = action.args[1]
      model[id] = m
      return model
  }
}

function renderLoop(model) {
  var tree = dashboard(model, channel)
  var node = createElement(tree)
  document.body.appendChild(node)

  csp.go(function* () {
    while (true) {
      var action = yield csp.take(channel)
      model = update(model, action)
      var updated = dashboard(model, channel)
        , patches = diff(tree, updated)
      node = patch(node, patches)
      tree = updated
    }
  });
}

renderLoop(model)

setInterval(() => {
  csp.go(function* (){
    yield csp.put(channel, {type: 'incr'})
  })
}, 1000)
