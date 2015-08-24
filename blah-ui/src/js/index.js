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
  switch (action.actionType) {
    case 'incr':
      model.count += 1
      return model
  }
}

function renderLoop(model) {
  var tree = dashboard(conn, model)
  var node = createElement(tree)
  document.body.appendChild(node)

  csp.go(function* () {
    while (true) {
      var action = yield csp.take(channel)
      model = update(model, action)
      var updated = dashboard(conn, model)
        , patches = diff(tree, updated)
      node = patch(node, patches)
      tree = updated
    }
  });
}

renderLoop(model)

setInterval(() => {
  csp.go(function* (){
    yield csp.put(channel, {actionType: 'incr'})
  })
}, 1000)
