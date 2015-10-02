import 'babelify/polyfill'
import {diff, patch} from 'virtual-dom'
import createElement from 'virtual-dom/create-element'
import csp from 'js-csp'
import connect from './connection'
import dashboard from './dashboard'
import * as ctrl from './ctrl'
import {SERVING_WS_URL} from './config'

var conn = connect(SERVING_WS_URL)
var channel = csp.chan()
var model = {count: 0}

conn.on('count', function() {
  console.log('count')
})

function update(model, action) {
  return ctrl[action.type].apply(null, [model].concat(action.args))
}

function renderLoop(model) {
  var tree = dashboard(model, channel)
  var node = createElement(tree)
  document.body.appendChild(node)

  csp.go(function*() {
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
  csp.go(function*(){
    yield csp.put(channel, {type: 'incr'})
  })
}, 1000)
