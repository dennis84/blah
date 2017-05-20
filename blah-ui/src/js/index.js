var snabbdom = require('snabbdom')
var patch = snabbdom.init([
  require('snabbdom/modules/class').default,
  require('snabbdom/modules/props').default,
  require('snabbdom/modules/style').default,
  require('snabbdom/modules/eventlisteners').default
])

var listen = require('./events')
var ctrl = require('./ctrl')
var pageviews = require('./ui/pageviews')
var user = require('./ui/user')
var misc = require('./ui/misc')
var people = require('./ui/people')
var funnel = require('./ui/funnel')
var segmentation = require('./ui/segmentation')
var worldMap = require('./ui/world-map')
var jobs = require('./ui/jobs')
var events = listen()

var state = {
  path: location.hash,
  isNavVisble: false,
  isDropdownVisble: false,
}

var vnode = render(state, update, events)
var node = document.getElementById('container')
patch(node, vnode)

function update(fn) {
  var args = [].slice.call(arguments, 1)
  state = fn.apply(null, [state].concat(args))
  vnode = patch(vnode, render(state, update, events))
}

function render() {
  var fn = pageviews
  if('#/pageviews' === state.path) fn = pageviews
  else if('#/user' === state.path) fn = user
  else if('#/misc' === state.path) fn = misc
  else if('#/people' === state.path) fn = people
  else if('#/funnel' === state.path) fn = funnel
  else if('#/segmentation' === state.path) fn = segmentation
  else if('#/world-map' === state.path) fn = worldMap
  else if('#/jobs' === state.path) fn = jobs
  return fn.apply(null, arguments)
}

window.addEventListener('hashchange', function() {
  var container = document.createElement('div')

  destroy(vnode)
  state = ctrl.path(state, location.hash)
  vnode = patch(container, render(state, update, events))

  document.body.innerHTML = ''
  document.body.appendChild(vnode.elm)
})

function destroy(node) {
  if(node.data.hook && node.data.hook.destroy) {
    node.data.hook.destroy(node)
  }

  for(var i in node.children) {
    destroy(node.children[i])
  }
}
