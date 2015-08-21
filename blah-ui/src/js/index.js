var diff = require('virtual-dom/diff')
var patch = require('virtual-dom/patch')
var createElement = require('virtual-dom/create-element')
var h = require('virtual-dom/h')

var connect = require('./connection')
var data = require('./data')
var dashboard = require('./dashboard')

var state = data.state()
var tree = dashboard(state)
var node = createElement(tree)
document.body.appendChild(node)

var conn = connect('ws://localhost:8001/ws')
conn.on('opened', function() {
  console.log('opened')
})

conn.on('count', function(data) {
  console.log('count', data)
})

// UPDATE
// var updated = render()
//   , patches = diff(tree, updated)
// node = patch(node, patches)
// tree = updated
