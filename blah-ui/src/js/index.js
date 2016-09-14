import 'babel-polyfill'
import snabbdom from 'snabbdom'
import classModule from 'snabbdom/modules/class'
import propsModule from 'snabbdom/modules/props'
import styleModule from 'snabbdom/modules/style'
import eventlistenersModule from 'snabbdom/modules/eventlisteners'
import xtend from 'xtend'
import {SERVING_WS_URL} from './config'
import connect from './connection'
import Storage from './storage'
import * as ctrl from './ctrl'
import pageviews from './ui/pageviews'
import user from './ui/user'
import misc from './ui/misc'
import people from './ui/people'
import funnel from './ui/funnel'
import segmentation from './ui/segmentation'
import worldMap from './ui/world-map'
import jobs from './ui/jobs'

var patch = snabbdom.init([
  classModule,
  propsModule,
  styleModule,
  eventlistenersModule
])

var conn = connect(SERVING_WS_URL)
var storage = new Storage(window.localStorage)

var state = xtend({
  path: location.hash,
  theme: 'light'
}, storage.get('settings'))

var vnode = render(state, update, conn, storage)

function update(fn, ...args) {
  if(typeof fn === 'function') {
    state = fn(state, ...args)
  }

  vnode = patch(vnode, render(state, update, conn, storage))
}

function render() {
  if('#/pageviews' === state.path) return pageviews(...arguments)
  else if('#/user' === state.path) return user(...arguments)
  else if('#/misc' === state.path) return misc(...arguments)
  else if('#/people' === state.path) return people(...arguments)
  else if('#/funnel' === state.path) return funnel(...arguments)
  else if('#/segmentation' === state.path) return segmentation(...arguments)
  else if('#/world-map' === state.path) return worldMap(...arguments)
  else if('#/jobs' === state.path) return jobs(...arguments)
  else return pageviews(...arguments)
}

window.addEventListener('hashchange', () => {
  var container = document.createElement('div')

  state = ctrl.path(state, location.hash)
  vnode = patch(container, render(state, update, conn, storage))

  document.body.innerHTML = ''
  document.body.appendChild(vnode.elm)
})

patch(document.getElementById('container'), vnode)
