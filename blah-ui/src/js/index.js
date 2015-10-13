import 'babelify/polyfill'
import {diff, patch} from 'virtual-dom'
import createElement from 'virtual-dom/create-element'
import csp from 'js-csp'
import connect from './connection'
import dashboard from './dashboard'
import * as ctrl from './ctrl'
import {SERVING_WS_URL} from './config'

var conn = connect(SERVING_WS_URL)
var chan = csp.chan()
var model = {}

function update(model, action) {
  return ctrl[action.type].apply(null, [model].concat(action.args))
}

function renderLoop(model) {
  var tree = dashboard(model, chan, conn)
  var node = createElement(tree)
  document.body.appendChild(node)

  csp.go(function*() {
    while(true) {
      var action = yield csp.take(chan)
      model = update(model, action)
      var updated = dashboard(model, chan, conn)
        , patches = diff(tree, updated)
      node = patch(node, patches)
      tree = updated
    }
  });
}

renderLoop(model)
