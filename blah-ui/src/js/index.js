var snabbdom = require('snabbdom')
var patch = snabbdom.init([
  require('snabbdom/modules/class').default,
  require('snabbdom/modules/props').default,
  require('snabbdom/modules/style').default,
  require('snabbdom/modules/eventlisteners').default
])
var xtend = require('xtend')
var listen = require('./events')
var Storage = require('./storage')
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
var storage = new Storage(window.localStorage)

var state = xtend({
  path: location.hash,
  theme: 'light'
}, storage.get('settings'))

var vnode = render(state, update, events, storage)

function update(fn) {
  var args = [].slice.call(arguments, 1)
  if(typeof fn === 'function') {
    state = fn.apply(null, [state].concat(args))
  }

  vnode = patch(vnode, render(state, update, events, storage))
}

function render() {
  if('#/pageviews' === state.path) return pageviews.apply(null, arguments)
  else if('#/user' === state.path) return user.apply(null, arguments)
  else if('#/misc' === state.path) return misc.apply(null, arguments)
  else if('#/people' === state.path) return people.apply(null, arguments)
  else if('#/funnel' === state.path) return funnel.apply(null, arguments)
  else if('#/segmentation' === state.path) return segmentation.apply(null, arguments)
  else if('#/world-map' === state.path) return worldMap.apply(null, arguments)
  else if('#/jobs' === state.path) return jobs.apply(null, arguments)
  else return pageviews.apply(null, arguments)
}

window.addEventListener('hashchange', function() {
  var container = document.createElement('div')

  destroy(vnode)
  state = ctrl.path(state, location.hash)
  vnode = patch(container, render(state, update, events, storage))

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

patch(document.getElementById('container'), vnode)
