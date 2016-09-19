var snabbdom = require('snabbdom')
var patch = snabbdom.init([
  require('snabbdom/modules/class'),
  require('snabbdom/modules/props'),
  require('snabbdom/modules/style'),
  require('snabbdom/modules/eventlisteners')
])
var xtend = require('xtend')
var config = require('./config')
var listen = require('./websocket')
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

var ws = listen(config.SERVING_WS_URL)
var storage = new Storage(window.localStorage)

var state = xtend({
  path: location.hash,
  theme: 'light'
}, storage.get('settings'))

var vnode = render(state, update, ws, storage)

function update(fn) {
  var args = [].slice.call(arguments, 1)
  if(typeof fn === 'function') {
    state = fn.apply(null, [state].concat(args))
  }

  vnode = patch(vnode, render(state, update, ws, storage))
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

  state = ctrl.path(state, location.hash)
  vnode = patch(container, render(state, update, ws, storage))

  document.body.innerHTML = ''
  document.body.appendChild(vnode.elm)
})

patch(document.getElementById('container'), vnode)
