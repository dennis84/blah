var diff = require('virtual-dom/diff')
var patch = require('virtual-dom/patch')
var createElement = require('virtual-dom/create-element')
var h = require('virtual-dom/h')

var connect = require('./connection')
var conn = connect('ws://localhost:8001/ws')
var dashboard = require('./dashboard')

var tree = dashboard(conn, 'initial')
var node = createElement(tree)
document.body.appendChild(node)

var i = 0
setInterval(function() {
  var updated = dashboard(conn, String(i))
    , patches = diff(tree, updated)
  node = patch(node, patches)
  tree = updated
  i ++
}, 1000)
