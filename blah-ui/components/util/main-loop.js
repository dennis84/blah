var raf = require('raf')
var virtualDom = require('virtual-dom')
var create = virtualDom.create
var diff = virtualDom.diff
var patch = virtualDom.patch

function main(initialState, view, target, opts) {
  var currentState = initialState
  var redrawScheduled = false
  var tree = view(currentState)
  var rootNode = create(tree)

  target.appendChild(rootNode)

  var inRenderingTransaction = false
  currentState = null

  function update(state, reset) {
    if(inRenderingTransaction) return
    if(currentState === null && !redrawScheduled) {
      redrawScheduled = true
      raf(redraw.bind(null, reset))
    }

    currentState = state
    loop.state = state
  }

  function redraw(reset) {
    redrawScheduled = false
    if(currentState === null) return

    inRenderingTransaction = true
    var newTree = view(currentState)

    if(reset) {
      inRenderingTransaction = false
      var newRoot = create(newTree, opts)
      target.replaceChild(newRoot, rootNode)
      rootNode = newRoot
    } else {
      var patches = diff(tree, newTree, opts)
      inRenderingTransaction = false
      rootNode = patch(rootNode, patches, opts)
    }

    tree = newTree
    currentState = null
  }

  var loop = {
    state: initialState,
    update: update
  }

  return loop
}

module.exports = main
