import {diff, patch} from 'virtual-dom'
import createElement from 'virtual-dom/create-element'
import connect from './connection'
import dashboard from './dashboard'

var conn = connect('ws://localhost:8001/ws')

var tree = dashboard(conn, 'initial')
var node = createElement(tree)
document.body.appendChild(node)

var i = 0
setInterval(() => {
  var updated = dashboard(conn, String(i))
    , patches = diff(tree, updated)
  node = patch(node, patches)
  tree = updated
  i ++
}, 1000)
